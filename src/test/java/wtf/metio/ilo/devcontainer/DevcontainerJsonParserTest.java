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
import wtf.metio.ilo.test.TestResources;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static wtf.metio.ilo.devcontainer.DevcontainerJsonParser.findJson;
import static wtf.metio.ilo.test.TestResources.testResources;

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
        () -> assertEquals("my.dockerfile", devcontainer.dockerFile, "dockerFile"),
        () -> assertEquals("testUser", devcontainer.remoteUser, "remoteUser"),
        () -> assertEquals("root", devcontainer.containerUser, "containerUser"),
        () -> assertEquals("/home/testUser/project", devcontainer.workspaceFolder, "workspaceFolder"),
        () -> assertTrue(devcontainer.overrideCommand, "overrideCommand"),
        () -> assertIterableEquals(List.of(12345, 9876), devcontainer.forwardPorts, "forwardPorts"),
        () -> assertIterableEquals(List.of("--pull"), devcontainer.runArgs, "runArgs"),
        () -> assertEquals("value", devcontainer.remoteEnv.get("key"), "remoteEnv"),
        () -> assertEquals("yes", devcontainer.containerEnv.get("CI"), "containerEnv"),
        () -> assertEquals("other.dockerfile", devcontainer.build.dockerFile, "build.dockerFile"),
        () -> assertEquals(".", devcontainer.build.context, "build.context"),
        () -> assertEquals("dev", devcontainer.build.target, "build.target"),
        () -> assertEquals("value", devcontainer.build.args.get("some"), "build.args"));
  }

  @Test
  @DisplayName("can parse devcontainer.json for compose")
  void shouldParseComposeJson() {
    final var devcontainer = DevcontainerJsonParser.parseJson(findJsonIn("compose"));
    assertAll("compose",
        () -> assertEquals("some-file.yml", devcontainer.dockerComposeFile, "dockerComposeFile"),
        () -> assertEquals("dev", devcontainer.service, "service"),
        () -> assertEquals("my-name", devcontainer.name, "name"),
        () -> assertIterableEquals(List.of("first", "second"), devcontainer.runServices, "runServices"));
  }

  private Path findJsonIn(final String testDirectory) {
    return findJson(testResources(DevcontainerJsonParser.class).resolve(testDirectory));
  }

}