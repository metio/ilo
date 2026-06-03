/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.compose;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import wtf.metio.ilo.cli.Keepalive;
import wtf.metio.ilo.errors.RuntimeIOException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * Writes a Compose override file that replaces a service's entrypoint and command with the
 * {@link Keepalive}. Layered after the project's own compose file(s), it makes the attached service
 * stay running for reuse without editing the user's files — the same approach the official
 * {@code @devcontainers/cli} uses for Compose-based dev containers.
 */
final class ComposeOverride {

  private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());

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
      final var file = Files.createTempFile("ilo-compose-keepalive-", ".yml");
      file.toFile().deleteOnExit();
      YAML.writeValue(file.toFile(), definition);
      return file.toString();
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  private ComposeOverride() {
    // utility class
  }

}
