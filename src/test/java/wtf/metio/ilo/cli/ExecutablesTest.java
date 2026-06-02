/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;
import uk.org.webcompere.systemstubs.stream.SystemOut;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

  @Test
  @DisplayName("uses the tool name verbatim on non-Windows hosts")
  void candidateNamesOnNonWindows() {
    assertEquals(List.of("docker"), Executables.candidateNames("docker", false, List.of(".EXE")));
  }

  @Test
  @DisplayName("appends every executable extension on Windows hosts")
  void candidateNamesOnWindows() {
    assertEquals(
        List.of("docker.EXE", "docker.CMD"),
        Executables.candidateNames("docker", true, List.of(".EXE", ".CMD")));
  }

  @Test
  @DisplayName("keeps a tool name that already has an extension on Windows hosts")
  void candidateNamesOnWindowsWithExtension() {
    assertEquals(List.of("pwsh.exe"), Executables.candidateNames("pwsh.exe", true, List.of(".EXE")));
  }

  @Test
  @DisplayName("detects a file extension")
  void detectsExtension() {
    assertTrue(Executables.hasExtension("pwsh.exe"));
  }

  @Test
  @DisplayName("detects a missing file extension")
  void detectsMissingExtension() {
    assertFalse(Executables.hasExtension("docker"));
  }

  @Test
  @DisplayName("does not treat a leading dot as an extension")
  void detectsLeadingDotIsNoExtension() {
    assertFalse(Executables.hasExtension(".bashrc"));
  }

  @Test
  @DisplayName("ignores dots in parent directories when detecting an extension")
  void detectsExtensionFromFileNameOnly() {
    assertFalse(Executables.hasExtension("some.dir/docker"));
  }

  @Test
  @DisplayName("falls back to default extensions when PATHEXT is empty")
  void parsesDefaultExtensions() {
    assertEquals(List.of(".COM", ".EXE", ".BAT", ".CMD"), Executables.parseExtensions(null));
    assertEquals(List.of(".COM", ".EXE", ".BAT", ".CMD"), Executables.parseExtensions("  "));
  }

  @Test
  @DisplayName("parses PATHEXT into individual extensions")
  void parsesConfiguredExtensions() {
    assertEquals(List.of(".EXE", ".CMD"), Executables.parseExtensions(".EXE; .CMD ;"));
  }

  @Test
  @DisplayName("reads executable extensions from the environment")
  void readsExecutableExtensions() {
    assertFalse(Executables.executableExtensions().isEmpty());
  }

  @Test
  @DisplayName("recognizes a Windows host")
  void recognizesWindows(final SystemProperties properties) throws Exception {
    properties.set("os.name", "Windows 11");
    assertTrue(Executables.isWindows());
  }

  @Test
  @DisplayName("recognizes a non-Windows host")
  void recognizesNonWindows(final SystemProperties properties) throws Exception {
    properties.set("os.name", "Linux");
    assertFalse(Executables.isWindows());
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("resolves a Windows executable by its extension")
  void resolvesWindowsExecutableByExtension(
      @TempDir final Path directory,
      final EnvironmentVariables environment,
      final SystemProperties properties) throws Exception {
    final var executable = Files.createFile(directory.resolve("faketool.exe"));
    assertTrue(executable.toFile().setExecutable(true));
    environment.set("PATH", directory.toString());
    environment.set("PATHEXT", ".exe");
    properties.set("os.name", "Windows 10");

    assertTrue(Executables.of("faketool").isPresent());
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("does not resolve a Windows executable without its extension on non-Windows hosts")
  void doesNotResolveWindowsExecutableOnNonWindows(
      @TempDir final Path directory,
      final EnvironmentVariables environment,
      final SystemProperties properties) throws IOException {
    final var executable = Files.createFile(directory.resolve("faketool.exe"));
    assertTrue(executable.toFile().setExecutable(true));
    environment.set("PATH", directory.toString());
    properties.set("os.name", "Linux");

    assertTrue(Executables.of("faketool").isEmpty());
  }

}
