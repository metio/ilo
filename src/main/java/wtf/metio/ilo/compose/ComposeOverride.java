/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.compose;

import tools.jackson.core.JacksonException;
import tools.jackson.dataformat.yaml.YAMLMapper;
import wtf.metio.ilo.cli.Keepalive;
import wtf.metio.ilo.errors.RuntimeIOException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Map;

/**
 * Writes a Compose override file that replaces a service's entrypoint and command with the
 * {@link Keepalive}. Layered after the project's own compose file(s), it makes the attached service
 * stay running for reuse without editing the user's files — the same approach the official
 * {@code @devcontainers/cli} uses for Compose-based dev containers.
 */
final class ComposeOverride {

  private static final YAMLMapper YAML = YAMLMapper.builder().build();

  // The temp file lives in the world-writable system temp directory, so it is created atomically with
  // owner-only permissions to keep its contents private. POSIX permissions do not exist on every file
  // system (e.g. Windows), where temp files already sit in a per-user location, so they are omitted.
  private static final FileAttribute<?>[] OWNER_ONLY =
      FileSystems.getDefault().supportedFileAttributeViews().contains("posix")
          ? new FileAttribute<?>[]{PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"))}
          : new FileAttribute<?>[0];

  /**
   * Writes the override for the given service to a temporary file and returns its path. The file is
   * removed when the JVM exits.
   *
   * @param service The service whose command is replaced with the keepalive.
   * @return The absolute path of the written override file.
   */
  static String write(final String service) {
    final var definition = Map.of(
        "services", Map.of(
            service, Map.of(
                "entrypoint", Keepalive.command(),
                "command", List.of())));
    try {
      final var file = Files.createTempFile("ilo-compose-keepalive-", ".yml", OWNER_ONLY);
      file.toFile().deleteOnExit();
      YAML.writeValue(file.toFile(), definition);
      return file.toString();
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    } catch (final JacksonException exception) {
      // The serialized value is a constant map of strings and lists, so serialization itself cannot
      // fail; a failure here is the underlying write to the temp file, which the runtime wraps as an
      // unchecked JacksonException.
      throw new RuntimeIOException(new IOException(exception));
    }
  }

  private ComposeOverride() {
    // utility class
  }

}
