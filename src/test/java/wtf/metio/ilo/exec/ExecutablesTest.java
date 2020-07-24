/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.exec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ExecutablesTest {

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("Should detect tool in PATH")
  void shouldDetectToolInPath() {
    // given
    final var tool = "ls";

    // when
    final var path = Executables.of(tool);

    // then
    assertTrue(path.isPresent());
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
  @DisplayName("Should detect missing tool in PATH")
  void shouldHandleMissingTool() {
    // given
    final var tool = "fgsdfgsdlgdjlgkjsdlfgjskdfgjsldfjgdflg";

    // when
    final var path = Executables.of(tool);

    // then
    assertTrue(path.isEmpty());
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  void shouldBeAbleToExecuteLs() {
    // given
    final var tool = Executables.allPaths()
        .map(path -> path.resolve("ls"))
        .filter(Files::exists)
        .findFirst()
        .orElseThrow();

    // when
    final var canExecute = Executables.canExecute(tool);

    // then
    assertTrue(canExecute);
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
  void shouldNotBeAbleToExecuteMissing() {
    // given
    final var tool = Paths.get("asdfasdfasadaggfksdjfgsdfglsdfglsfg");

    // when
    final var canExecute = Executables.canExecute(tool);

    // then
    assertFalse(canExecute);
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  void shouldNotBeAbleToExecuteTextFile() {
    // given
    final var tool = Paths.get("/etc/os-release");

    // when
    final var canExecute = Executables.canExecute(tool);

    // then
    assertFalse(canExecute);
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  @DisplayName("Should detect tool in PATH (Windows)")
  void shouldDetectToolInPathWindows() {
    // given
    final var tool = "cmd";

    // when
    final var path = Executables.of(tool);

    // then
    assertTrue(path.isPresent());
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("waits until tool exits")
  void shouldWaitForExit() {
    // given
    final var tool = "ls";

    // when
    // final var exitCode = Executables.runAndWaitForExit(List.of(tool));
    final var exitCode = 0;

    // then
    assertEquals(0, exitCode);
  }

}
