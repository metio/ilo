/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.errors.DevcontainerJsonMissingException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static wtf.metio.ilo.devcontainer.DevcontainerJsonParser.findJson;

@DisplayName("DevcontainerJsonParser")
class DevcontainerJsonParserTest {

  @Test
  @DisplayName("finds devcontainer.json in project root")
  void shouldFindRootJson() {
    assertTrue(Files.exists(findJsonIn("root")));
  }

  @Test
  @DisplayName("finds devcontainer.json in .devcontainer folder")
  void shouldFindNestedJson() {
    assertTrue(Files.exists(findJsonIn("nested")));
  }

  @Test
  @DisplayName("throws exception for missing devcontainer.json")
  void shouldNotFindMissingJson() {
    assertThrows(DevcontainerJsonMissingException.class, () -> findJsonIn("not-found"));
  }

  @Test
  @DisplayName("can parse devcontainer.json for shell")
  void shouldParseShellJson() {
    final var devcontainer = DevcontainerJsonParser.parseJson(findJsonIn("shell"));
    assertAll("shell",
        () -> assertEquals("example:123", devcontainer.image, "image"),
        () -> assertEquals("my.dockerfile", devcontainer.dockerFile, "dockerFile"));
  }

  @Test
  @DisplayName("can parse devcontainer.json for compose")
  void shouldParseComposeJson() {
    final var devcontainer = DevcontainerJsonParser.parseJson(findJsonIn("compose"));
    assertAll("compose",
        () -> assertEquals("some-file.yml", devcontainer.dockerComposeFile, "dockerComposeFile"),
        () -> assertEquals("dev", devcontainer.service, "service"),
        () -> assertEquals("my-name", devcontainer.name, "name"));
  }

  private Path findJsonIn(final String testDirectory) {
    final var testResources = Paths.get("src/test/resources/");
    final var testRootDirectory = DevcontainerJsonParser.class.getName().replace(".", "/");
    return findJson(testResources.resolve(testRootDirectory).resolve(testDirectory));
  }

}