/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.compose;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

@DisplayName("ComposeOverride")
class ComposeOverrideTest {

  @Test
  @DisplayName("writes a keepalive override that replaces the service's entrypoint and command")
  void writesKeepaliveOverride() throws Exception {
    final var path = ComposeOverride.write("dev");
    try {
      final var tree = new ObjectMapper(new YAMLFactory()).readTree(new File(path));
      final var service = tree.path("services").path("dev");
      final var entrypoint = service.path("entrypoint");
      assertEquals("sh", entrypoint.get(0).asText());
      assertEquals("-c", entrypoint.get(1).asText());
      assertTrue(entrypoint.get(2).asText().contains("trap 'exit 0' TERM INT"), entrypoint.toString());
      assertTrue(service.path("command").isEmpty(), "command should be cleared");
    } finally {
      Files.deleteIfExists(Path.of(path));
    }
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("creates the override readable only by its owner in the public temp directory")
  void writesOwnerOnlyFile() throws Exception {
    final var path = ComposeOverride.write("dev");
    try {
      assertEquals("rw-------", PosixFilePermissions.toString(Files.getPosixFilePermissions(Path.of(path))));
    } finally {
      Files.deleteIfExists(Path.of(path));
    }
  }

}
