/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import wtf.metio.ilo.errors.RuntimeIOException;
import wtf.metio.ilo.utils.Strings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Builds the derived image that aligns a non-root container user's UID/GID with the host's, the way
 * the official {@code @devcontainers/cli} does for rootful Docker (which has no user namespace to map
 * through). The container then runs as that user, keeping its name, home, and passwd entry, while the
 * files it writes into the mounted project land owned by the host user.
 */
final class RemoteUserImage {

  // Run as a single line so the generated Containerfile needs no line-continuation escaping. The user
  // is changed in place: '-o' tolerates a UID/GID already taken by another account, and a missing user
  // or a base image without usermod/groupmod (e.g. busybox) is reported and skipped rather than failing
  // the build, so the worst case is unchanged ownership rather than an unusable image.
  private static final String REMAP =
      "if id %USER% >/dev/null 2>&1; then "
          + "if command -v usermod >/dev/null 2>&1 && command -v groupmod >/dev/null 2>&1; then "
          + "groupmod -o -g %NEW_GID% \"$(id -gn %USER%)\" || true; "
          + "usermod -o -u %NEW_UID% -g %NEW_GID% %USER%; "
          + "chown -R %NEW_UID%:%NEW_GID% \"$(getent passwd %USER% | cut -d: -f6)\" || true; "
          + "else echo \"ilo: usermod/groupmod unavailable; skipping UID/GID remap for %USER%\"; fi; "
          + "else echo \"ilo: user %USER% not found; skipping UID/GID remap\"; fi";

  /**
   * Extracts the user name from a {@code {{.Config.User}}} image-inspect result. A {@code user:group}
   * spec keeps only the user; an empty result (the image declares no user, i.e. root) yields null.
   *
   * @param inspectOutput The captured inspect output.
   * @return The configured user name, or null when none is set.
   */
  static String imageUser(final String inspectOutput) {
    final var trimmed = inspectOutput == null ? "" : inspectOutput.strip();
    if (trimmed.isEmpty()) {
      return null;
    }
    final var name = trimmed.split(":", 2)[0];
    return name.isBlank() ? null : name;
  }

  /**
   * Generates the derived Containerfile: layered straight onto a base image, or appended to an
   * existing Containerfile so the user's own build runs first.
   *
   * @param fromImage          The base image to build from, or null when building on a Containerfile.
   * @param baseContainerfile  The existing Containerfile's contents, or null when building on an image.
   * @param remoteUser         The user to remap and run as.
   * @param hostUid            The host UID to give the user.
   * @param hostGid            The host GID to give the user.
   * @return The generated Containerfile contents.
   */
  static String containerfile(final String fromImage, final String baseContainerfile,
      final String remoteUser, final String hostUid, final String hostGid) {
    final var base = fromImage != null ? "FROM " + fromImage : baseContainerfile;
    final var run = REMAP
        .replace("%USER%", remoteUser)
        .replace("%NEW_UID%", hostUid)
        .replace("%NEW_GID%", hostGid);
    return base + "\nUSER root\nRUN " + run + "\nUSER " + remoteUser + "\n";
  }

  /**
   * A repository tag derived from the generated contents, so an unchanged definition reuses the same
   * image (and container) while any change to the base, user, or host IDs produces a new one.
   *
   * @param content The generated Containerfile contents.
   * @return The derived image tag.
   */
  static String tag(final String content) {
    return "ilo-remote-user:" + hash(content);
  }

  /**
   * Generates the derived Containerfile for the given options and repoints them at it: the build runs
   * the generated file, tags the result deterministically, and (for an image-based environment, which
   * has no build context of its own) builds from the generated file's directory.
   *
   * @param options The options to rewrite in place.
   * @param hostUid The host UID to give the user.
   * @param hostGid The host GID to give the user.
   */
  static void rewrite(final ShellOptions options, final String hostUid, final String hostGid) {
    final var onImage = Strings.isBlank(options.containerfile);
    final var content = containerfile(
        onImage ? options.image : null,
        onImage ? null : read(options.containerfile),
        options.remoteUser, hostUid, hostGid);
    final var file = write(content);
    options.image = tag(content);
    options.containerfile = file.toString();
    if (Strings.isBlank(options.context)) {
      options.context = file.getParent().toString();
    }
  }

  // Written to a deterministic path so the same definition yields the same file across runs, keeping
  // the container fingerprint (which includes the Containerfile path and contents) stable for reuse.
  private static Path write(final String content) {
    final var file = Paths.get(System.getProperty("java.io.tmpdir"), "ilo-remote-user-" + hash(content) + ".containerfile");
    try {
      Files.writeString(file, content);
      return file;
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  private static String read(final String containerfile) {
    try {
      return Files.readString(Paths.get(containerfile));
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  private static String hash(final String content) {
    return Integer.toHexString(content.hashCode() & 0x7fffffff);
  }

  private RemoteUserImage() {
    // utility class
  }

}
