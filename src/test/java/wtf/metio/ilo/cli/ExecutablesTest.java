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
import uk.org.webcompere.systemstubs.stream.SystemErr;
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
  @DisplayName("writes the debug trace to stderr, not stdout")
  void shouldWriteDebugMessageToStandardError(final SystemErr systemErr, final SystemOut systemOut) {
    // given
    final var tool = "ls";

    // when
    Executables.runAndWaitForExit(List.of(tool), true);

    // then
    assertEquals("ilo executes: ls\n", systemErr.getText());
    assertEquals("", systemOut.getText());
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
  @DisplayName("yields no paths when PATH is unset")
  void allPathsWithoutPathVariable() {
    assertEquals(0, Executables.allPaths(null).count());
  }

  @Test
  @DisplayName("splits PATH into individual directories")
  void allPathsSplitsEntries() {
    final var path = "/usr/bin" + java.io.File.pathSeparator + "/usr/local/bin";
    assertEquals(2, Executables.allPaths(path).count());
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
  @DisplayName("captures the standard output of a command")
  void readsStandardOutput() {
    assertEquals("hello", Executables.runAndReadOutput(java.time.Duration.ofSeconds(10), "sh", "-c", "echo hello"));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("does not deadlock when a command floods stderr before writing stdout")
  void doesNotDeadlockOnStderrFlood() {
    // ~100 KB to stderr exceeds the OS pipe buffer; only because stderr is not captured into a pipe
    // ilo must drain does this finish. The 15s bound turns a regression into a failure, never a hang.
    final var output = Executables.runAndReadOutput(java.time.Duration.ofSeconds(15), "sh", "-c",
        "i=0; while [ $i -lt 2000 ]; do echo eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee 1>&2; i=$((i+1)); done; echo DONE");
    assertEquals("DONE", output);
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("reports an interruption while waiting for a command")
  void reportsInterruptionWhileWaiting() throws InterruptedException {
    final var thrown = new java.util.concurrent.atomic.AtomicReference<Throwable>();
    final var interruptPreserved = new java.util.concurrent.atomic.AtomicBoolean();
    final var worker = new Thread(() -> {
      try {
        Executables.runAndReadOutput(java.time.Duration.ofSeconds(30), "sh", "-c", "sleep 30");
      } catch (final Throwable throwable) {
        thrown.set(throwable);
        // waitFor clears the interrupt flag, so its being set again proves it was restored.
        interruptPreserved.set(Thread.currentThread().isInterrupted());
      }
    });
    worker.start();
    Thread.sleep(500); // let the worker reach Process.waitFor
    worker.interrupt();
    worker.join(10_000);

    assertTrue(thrown.get() instanceof wtf.metio.ilo.errors.UnexpectedInterruptionException,
        "expected UnexpectedInterruptionException but was " + thrown.get());
    assertTrue(interruptPreserved.get(), "the interrupt flag should be restored");
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("terminates a command that exceeds the timeout")
  void terminatesOnTimeout() {
    final var start = System.nanoTime();
    assertThrows(wtf.metio.ilo.errors.CommandTimedOutException.class,
        () -> Executables.runAndReadOutput(java.time.Duration.ofMillis(300), "sh", "-c", "sleep 30"));
    final var elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0;
    assertTrue(elapsedSeconds < 10, "should return promptly, took " + elapsedSeconds + "s");
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
