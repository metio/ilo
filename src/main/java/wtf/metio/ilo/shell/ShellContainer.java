/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.cli.ContainerNaming;
import wtf.metio.ilo.utils.Strings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Derives the name of a session's container. The name is {@code ilo-<slug>-<fingerprint>}: the slug
 * is the (sanitised) project directory name for readability in {@code docker ps}, and the fingerprint
 * is a hash of everything that defines the container — the project path, the image and build inputs
 * (including the Containerfile's contents), the run configuration, and any extra identity source (for
 * a devcontainer, the {@code devcontainer.json} itself). Any change to those produces a new name, so
 * a reused container is always one built from the exact same definition; a changed definition yields a
 * fresh container instead of silently reusing a stale one.
 */
final class ShellContainer {

  // Separator used to join the fingerprint fields before they are hashed into the container name.
  private static final String SEPARATOR = " ";

  static String name(final ShellOptions options, final String projectDir) {
    return ContainerNaming.containerName("ilo-" + slug(projectDir), fingerprint(options, projectDir));
  }

  // visible for testing
  static String slug(final String projectDir) {
    final var fileName = Paths.get(projectDir).getFileName();
    final var raw = Objects.isNull(fileName) ? projectDir : fileName.toString();
    final var cleaned = raw.toLowerCase(Locale.ROOT)
        .replaceAll("[^a-z0-9_.-]+", "-")
        .replaceAll("^-+|-+$", "");
    return cleaned.isBlank() ? "project" : cleaned;
  }

  // visible for testing
  static String fingerprint(final ShellOptions options, final String projectDir) {
    final var parts = new ArrayList<String>();
    parts.add(projectDir);
    parts.add(Objects.toString(options.image, ""));
    parts.add(Objects.toString(options.containerfile, ""));
    parts.add(Objects.toString(options.context, ""));
    parts.add(Objects.toString(options.workingDir, ""));
    parts.add(Objects.toString(options.hostname, ""));
    parts.add(Boolean.toString(options.mountProjectDir));
    parts.addAll(values(options.runtimeOptions));
    parts.addAll(values(options.runtimeBuildOptions));
    parts.addAll(values(options.runtimeRunOptions));
    parts.addAll(values(options.volumes));
    parts.addAll(values(options.variables));
    parts.addAll(values(options.ports));
    parts.add(containerfileContent(options.containerfile));
    parts.add(Objects.toString(options.identitySource, ""));
    return String.join(SEPARATOR, parts);
  }

  private static List<String> values(final List<String> values) {
    return Objects.isNull(values) ? List.of() : values;
  }

  // The Containerfile's contents are part of the identity so editing it produces a new container. It
  // is read best-effort: an unreadable path simply contributes nothing beyond the path string above.
  private static String containerfileContent(final String containerfile) {
    if (Strings.isBlank(containerfile)) {
      return "";
    }
    try {
      return Files.readString(Paths.get(containerfile));
    } catch (final IOException | RuntimeException unreadable) {
      return "";
    }
  }

  private ShellContainer() {
    // utility class
  }

}
