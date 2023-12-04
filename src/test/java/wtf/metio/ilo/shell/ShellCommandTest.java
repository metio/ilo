/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;
import wtf.metio.ilo.test.TestMethodSources;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ShellCommand")
@ExtendWith(SystemStubsExtension.class)
class ShellCommandTest extends TestMethodSources {

  private ShellCommand shell;
  private TestShellExecutor executor;
  private ShellOptions options;

  @BeforeEach
  void setUp() {
    executor = new TestShellExecutor();
    options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.workingDir = "some/dir";
    options.image = "fedora:latest";
    shell = new ShellCommand(executor);
    shell.options = options;
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with minimal settings")
  void dockerLikeMinimal(final String runtime) {
    final var tool = useRuntime(runtime);
    assertCommandLine(
        List.of(),
        List.of(),
        List.of(tool, "run", "--rm", "--workdir", options.workingDir, "--env", "ILO_CONTAINER=true", options.image),
        List.of());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with pull")
  void dockerLikeWithPull(final String runtime) {
    final var tool = useRuntime(runtime);
    options.pull = true;
    assertCommandLine(
        List.of(tool, "pull", options.image),
        List.of(),
        List.of(tool, "run", "--rm", "--workdir", options.workingDir, "--env", "ILO_CONTAINER=true", options.image),
        List.of());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with build")
  void dockerLikeWithBuild(final String runtime) {
    final var tool = useRuntime(runtime);
    options.containerfile = "Dockerfile";
    assertCommandLine(
        List.of(),
        List.of(tool, "build", "--file", options.containerfile, "--tag", options.image),
        List.of(tool, "run", "--rm", "--workdir", options.workingDir, "--env", "ILO_CONTAINER=true", options.image),
        List.of());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with cleanup")
  void dockerLikeWithCleanup(final String runtime) {
    final var tool = useRuntime(runtime);
    options.removeImage = true;
    assertCommandLine(
        List.of(),
        List.of(),
        List.of(tool, "run", "--rm", "--workdir", options.workingDir, "--env", "ILO_CONTAINER=true", options.image),
        List.of(tool, "rmi", options.image));
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with default settings")
  void dockerLikeWithDefaults(final String runtime, final SystemProperties properties) {
    properties.set("user.dir", "/some/folder");
    final var tool = useRuntime(runtime);
    options.interactive = true;
    options.mountProjectDir = true;
    options.workingDir = "";
    assertCommandLine(
        List.of(),
        List.of(),
        List.of(tool, "run", "--rm", "--volume", "/some/folder:/some/folder:z", "--workdir", "/some/folder", "--interactive", "--tty", "--env", "ILO_CONTAINER=true", options.image),
        List.of());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with runtime option")
  void dockerLikeWithRuntimeOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.runtimeOptions = List.of("--remote");
    assertCommandLine(
        List.of(),
        List.of(),
        List.of(tool, "--remote", "run", "--rm", "--workdir", options.workingDir, "--env", "ILO_CONTAINER=true", options.image),
        List.of());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with runtime pull option")
  void dockerLikeWithRuntimePullOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.pull = true;
    options.runtimePullOptions = List.of("--all-tags");
    assertCommandLine(
        List.of(tool, "pull", "--all-tags", options.image),
        List.of(),
        List.of(tool, "run", "--rm", "--workdir", options.workingDir, "--env", "ILO_CONTAINER=true", options.image),
        List.of());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with runtime build option")
  void dockerLikeWithRuntimeBuildOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.containerfile = "Dockerfile";
    options.runtimeBuildOptions = List.of("--squash-all");
    assertCommandLine(
        List.of(),
        List.of(tool, "build", "--file", options.containerfile, "--squash-all", "--tag", options.image),
        List.of(tool, "run", "--rm", "--workdir", options.workingDir, "--env", "ILO_CONTAINER=true", options.image),
        List.of());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with runtime run option")
  void dockerLikeWithRuntimeRunOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.runtimeRunOptions = List.of("--quiet");
    assertCommandLine(
        List.of(),
        List.of(),
        List.of(tool, "run", "--rm", "--quiet", "--workdir", options.workingDir, "--env", "ILO_CONTAINER=true", options.image),
        List.of());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with runtime cleanup option")
  void dockerLikeWithRuntimeCleanupOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.removeImage = true;
    options.runtimeCleanupOptions = List.of("--force");
    assertCommandLine(
        List.of(),
        List.of(),
        List.of(tool, "run", "--rm", "--workdir", options.workingDir, "--env", "ILO_CONTAINER=true", options.image),
        List.of(tool, "rmi", "--force", options.image));
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("fail early after pull")
  void failToPull(final String runtime) {
    final var tool = useRuntime(runtime);
    executor.exitCodes(1);
    options.pull = true;
    final var exitCode = shell.call();
    assertAll("command line",
        () -> assertEquals(1, exitCode, "exitCode"),
        () -> assertIterableEquals(List.of(tool, "pull", options.image), executor.pullArguments(), "pullArguments"),
        () -> noExecution(executor::buildArguments, "buildArguments"),
        () -> noExecution(executor::runArguments, "runArguments"),
        () -> noExecution(executor::cleanupArguments, "cleanupArguments"));
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("fail early after build")
  void failToBuild(final String runtime) {
    final var tool = useRuntime(runtime);
    executor.exitCodes(0, 1);
    options.containerfile = "Dockerfile";
    final var exitCode = shell.call();
    assertAll("command line",
        () -> assertEquals(1, exitCode, "exitCode"),
        () -> assertIterableEquals(List.of(), executor.pullArguments(), "pullArguments"),
        () -> assertIterableEquals(List.of(tool, "build", "--file", options.containerfile, "--tag", options.image), executor.buildArguments(), "buildArguments"),
        () -> noExecution(executor::runArguments, "runArguments"),
        () -> noExecution(executor::cleanupArguments, "cleanupArguments"));
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("fail early after run")
  void failToRun(final String runtime) {
    final var tool = useRuntime(runtime);
    executor.exitCodes(0, 0, 1);
    final var exitCode = shell.call();
    assertAll("command line",
        () -> assertEquals(1, exitCode, "exitCode"),
        () -> assertIterableEquals(List.of(), executor.pullArguments(), "pullArguments"),
        () -> assertIterableEquals(List.of(), executor.buildArguments(), "buildArguments"),
        () -> assertIterableEquals(List.of(tool, "run", "--rm", "--workdir", options.workingDir, "--env", "ILO_CONTAINER=true", options.image), executor.runArguments(), "runArguments"),
        () -> noExecution(executor::cleanupArguments, "cleanupArguments"));
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("return cleanup exit code")
  void failToClean(final String runtime) {
    final var tool = useRuntime(runtime);
    executor.exitCodes(0, 0, 0, 1);
    final var exitCode = shell.call();
    assertAll("command line",
        () -> assertEquals(1, exitCode, "exitCode"),
        () -> assertIterableEquals(List.of(), executor.pullArguments(), "pullArguments"),
        () -> assertIterableEquals(List.of(), executor.buildArguments(), "buildArguments"),
        () -> assertIterableEquals(List.of(tool, "run", "--rm", "--workdir", options.workingDir, "--env", "ILO_CONTAINER=true", options.image), executor.runArguments(), "runArguments"),
        () -> assertIterableEquals(List.of(), executor.cleanupArguments(), "cleanupArguments"));
  }

  private String useRuntime(final String runtime) {
    options.runtime = ShellRuntime.fromAlias(runtime);
    return options.runtime.aliases()[0];
  }

  private void assertCommandLine(
      final List<String> pullArguments,
      final List<String> buildArguments,
      final List<String> runArguments,
      final List<String> cleanupArguments) {
    final var exitCode = shell.call();
    assertAll("command line",
        () -> assertEquals(0, exitCode, "exitCode"),
        () -> assertIterableEquals(pullArguments, executor.pullArguments(), "pullArguments"),
        () -> assertIterableEquals(buildArguments, executor.buildArguments(), "buildArguments"),
        () -> assertIterableEquals(runArguments, executor.runArguments(), "runArguments"),
        () -> assertIterableEquals(cleanupArguments, executor.cleanupArguments(), "cleanupArguments"));
  }

  private void noExecution(final Supplier<List<String>> arguments, final String name) {
    assertThrows(IndexOutOfBoundsException.class, arguments::get, name);
  }

}
