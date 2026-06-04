/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import wtf.metio.ilo.os.OSSupport;
import wtf.metio.ilo.utils.Strings;

import java.util.List;
import java.util.function.Function;

/**
 * Resolves how the container user is aligned with the host user and, when a rootful Docker daemon
 * needs it, repoints the options at a derived image that performs the remap. Driven by
 * {@code --update-remote-user-uid} and {@code --remote-user}; the user defaults to the image's own
 * configured user when not given.
 */
final class RemoteUser {

  /**
   * Resolves the mapping for the given options and applies it in place: stores the effective user and
   * {@link RemoteUserMapping}, and for a {@link RemoteUserMapping#REMAP} builds the derived image.
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
    final var remoteUser = Strings.isNotBlank(options.remoteUser)
        ? options.remoteUser
        : RemoteUserImage.imageUser(capture.apply(inspect(tool, options.image)));
    options.remoteUser = remoteUser;
    options.userMapping = tool.remoteUserMapping(true, remoteUser, capture);
    if (options.userMapping == RemoteUserMapping.REMAP) {
      final var expand = OSSupport.expander();
      RemoteUserImage.rewrite(options, expand.expand("$(id -u)"), expand.expand("$(id -g)"));
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

  // Runs 'id <user>' in the image to read the user's UID/GID, overriding the image's entrypoint so the
  // command runs regardless of what the image normally launches.
  private static List<String> userIds(final ShellCLI tool, final String image, final String remoteUser) {
    return List.of(tool.name(), "run", "--rm", "--entrypoint", "id", image, remoteUser);
  }

  private RemoteUser() {
    // utility class
  }

}
