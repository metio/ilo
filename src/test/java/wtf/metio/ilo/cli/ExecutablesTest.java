/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemOut;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SystemStubsExtension.class)
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
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("waits until tool exits")
  void shouldWaitForExit() {
    // given
    final var tool = "ls";

    // when
    final var exitCode = Executables.runAndWaitForExit(List.of(tool), false);

    // then
    assertEquals(0, exitCode);
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("returns exit code on failures")
  void shouldReturnNonZeroExitCode() {
    // given
    final var arguments = List.of("ls", "--unknown");

    // when
    final var exitCode = Executables.runAndWaitForExit(arguments, false);

    // then
    assertTrue(0 < exitCode);
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("writes debug message to system.out")
  void shouldWriteDebugMessageToSystemOut(final SystemOut systemOut) {
    // given
    final var tool = "ls";

    // when
    Executables.runAndWaitForExit(List.of(tool), true);

    // then
    assertEquals("ilo executes: ls\n", systemOut.getText());
  }

  @Test
  @DisplayName("ignores empty lists")
  void shouldNoExecuteEmptyList() {
    // given
    final List<String> arguments = List.of();

    // when
    final var exitCode = Executables.runAndWaitForExit(arguments, false);

    // then
    assertEquals(0, exitCode);
  }

  @Test
  @DisplayName("ignores null lists")
  void shouldNoExecuteNullList() {
    // given
    final List<String> arguments = null;

    // when
    final var exitCode = Executables.runAndWaitForExit(arguments, false);

    // then
    assertEquals(0, exitCode);
  }

}
