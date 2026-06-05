/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import wtf.metio.ilo.errors.RuntimeIOException;
import wtf.metio.ilo.os.OSSupport;
import wtf.metio.ilo.utils.Strings;

import java.util.List;
import java.util.function.Function;

/**
 * Resolves how the container user is aligned with the host user and, when a rootful Docker daemon
 * needs it, repoints the options at a derived image that performs the remap. Driven by
 * {@code --update-remote-user-uid} and {@code --remote-user}; the user defaults to the image's own
 * configured user when not given.
 *
 * <p>The remap on rootful Docker is best-effort by design: when it cannot be performed safely (the
 * image lacks {@code usermod}/{@code groupmod}, the user name is not one we can remap, or the derived
 * image cannot be written) the mapping falls back to running as the bare host UID/GID, which still
 * keeps project files owned by the caller. Only when even the host UID/GID cannot be determined is the
 * alignment skipped — with a warning rather than a hard failure.
 */
final class RemoteUser {

  /**
   * Resolves the mapping for the given options and applies it in place: stores the effective user and
   * {@link RemoteUserMapping}, and for a rootful-Docker non-root user prepares the remap (or a safe
   * fallback).
   *
   * @param tool    The selected runtime.
   * @param options The options to resolve and rewrite in place.
   * @param capture Runs a command line and returns its standard output, used to inspect the image and
   *                probe the daemon.
   */
  static void resolve(final ShellCLI tool, final ShellOptions options, final Function<List<String>, String> capture) {
    if (!options.updateRemoteUserUID) {
      options.userMapping = RemoteUserMapping.NONE;
      return;
    }
    final var autoDetect = Strings.isBlank(options.remoteUser);
    final var remoteUser = autoDetect
        ? RemoteUserImage.imageUser(capture.apply(inspect(tool, options.image)))
        : options.remoteUser;
    options.remoteUser = remoteUser;
    options.userMapping = tool.remoteUserMapping(true, remoteUser, capture);
    if (options.userMapping == RemoteUserMapping.REMAP) {
      final var expand = OSSupport.expander();
      options.userMapping = prepareRemap(tool, options, capture,
          expand.expand("$(id -u)"), expand.expand("$(id -g)"));
    } else if (autoDetect && remoteUser == null && options.userMapping == RemoteUserMapping.NONE
        && !imagePresent(tool, options.image, capture)) {
      // Auto-detection read no user, but only because the image is not available yet (a present root
      // image legitimately reads as no user and needs no alignment). Tell the user rather than
      // silently skipping the alignment they asked for.
      warn("ilo: could not read the image's user because it is not available yet, so file-ownership "
          + "alignment is skipped this run; it applies once the image is cached, or pass --remote-user.");
    }
  }

  /**
   * Prepares the rootful-Docker remap, returning the mapping that can actually be applied: {@code REMAP}
   * with a derived image when possible, {@code HOST_USER} (run as the bare host UID/GID) when the remap
   * cannot be performed but ownership can still be fixed, or {@code NONE} (with a warning) when the host
   * UID/GID cannot even be determined.
   *
   * @param tool    The selected runtime.
   * @param options The options to rewrite in place when a derived image is built.
   * @param capture Runs a command line and returns its standard output.
   * @param hostUid The host UID, expanded from {@code $(id -u)}.
   * @param hostGid The host GID, expanded from {@code $(id -g)}.
   * @return The mapping to apply.
   */
  // visible for testing
  static RemoteUserMapping prepareRemap(final ShellCLI tool, final ShellOptions options,
      final Function<List<String>, String> capture, final String hostUid, final String hostGid) {
    if (!isNumeric(hostUid) || !isNumeric(hostGid)) {
      warn("ilo: could not determine your host UID/GID, so file-ownership alignment is skipped.");
      return RemoteUserMapping.NONE;
    }
    if (!isRemappableName(options.remoteUser) || !imageHasUserTools(tool, options.image, capture)) {
      warn("ilo: cannot remap '" + options.remoteUser + "' to your IDs in this image; running as your "
          + "host UID/GID instead so project files stay owned by you.");
      return RemoteUserMapping.HOST_USER;
    }
    try {
      RemoteUserImage.rewrite(options, hostUid, hostGid);
      return RemoteUserMapping.REMAP;
    } catch (final RuntimeIOException exception) {
      warn("ilo: could not prepare the remap image; running as your host UID/GID instead so project "
          + "files stay owned by you.");
      return RemoteUserMapping.HOST_USER;
    }
  }

  /**
   * Pins a keep-id mapping to the container user's own UID/GID by probing the image for them, so the
   * host user maps onto that user even when the host UID differs. Only podman supports the pinned form,
   * and the IDs are only needed when the container is created, so this runs solely for a podman
   * {@link RemoteUserMapping#KEEP_ID} that is about to be created. A failed probe leaves the IDs null,
   * which falls back to a plain keep-id namespace.
   *
   * @param tool     The selected runtime.
   * @param options  The options to pin in place.
   * @param capture  Runs a command line and returns its standard output.
   * @param creating Whether the container will be created (false when an existing one is reused).
   */
  static void pinKeepId(final ShellCLI tool, final ShellOptions options,
      final Function<List<String>, String> capture, final boolean creating) {
    if (!creating || options.userMapping != RemoteUserMapping.KEEP_ID || !tool.supportsKeepIdUid()) {
      return;
    }
    final var ids = capture.apply(userIds(tool, options.image, options.remoteUser));
    options.remoteUid = RemoteUserImage.userId(ids, "uid");
    options.remoteGid = RemoteUserImage.userId(ids, "gid");
  }

  // Asks the runtime for the image's configured user; all three runtimes accept the same inspect form.
  private static List<String> inspect(final ShellCLI tool, final String image) {
    return List.of(tool.name(), "image", "inspect", "--format", "{{.Config.User}}", image);
  }

  // Whether the image exists locally: its id is reported only for a present image, so a blank result
  // means the image has not been pulled or built yet.
  private static boolean imagePresent(final ShellCLI tool, final String image,
      final Function<List<String>, String> capture) {
    return Strings.isNotBlank(capture.apply(List.of(tool.name(), "image", "inspect", "--format", "{{.Id}}", image)));
  }

  // Whether the image ships usermod and groupmod, which the remap build needs; probed by running them
  // (which also pulls the image if absent). A base image without them (e.g. busybox/alpine) cannot be
  // remapped, so the caller falls back to running as the bare host UID/GID.
  private static boolean imageHasUserTools(final ShellCLI tool, final String image,
      final Function<List<String>, String> capture) {
    final var probe = List.of(tool.name(), "run", "--rm", "--entrypoint", "sh", image, "-c",
        "command -v usermod >/dev/null 2>&1 && command -v groupmod >/dev/null 2>&1 && echo ok");
    return "ok".equals(capture.apply(probe).strip());
  }

  // Runs 'id <user>' in the image to read the user's UID/GID, overriding the image's entrypoint so the
  // command runs regardless of what the image normally launches.
  private static List<String> userIds(final ShellCLI tool, final String image, final String remoteUser) {
    return List.of(tool.name(), "run", "--rm", "--entrypoint", "id", image, remoteUser);
  }

  private static boolean isNumeric(final String value) {
    return value != null && !value.isEmpty() && value.chars().allMatch(Character::isDigit);
  }

  // A user that can be remapped with usermod: a real login name, not a numeric id (usermod takes a
  // name) and free of characters that would break or inject into the generated Containerfile.
  private static boolean isRemappableName(final String user) {
    return user != null && user.matches("[A-Za-z_][A-Za-z0-9._-]*");
  }

  private static void warn(final String message) {
    System.err.println(message);
  }

  private RemoteUser() {
    // utility class
  }

}
