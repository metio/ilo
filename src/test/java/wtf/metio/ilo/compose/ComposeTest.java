/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wtf.metio.ilo.test.TestMethodSources;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Compose")
class ComposeTest extends TestMethodSources {

  private static final String DOCKER_COMPOSE_YML = "docker-compose.yml";

  private Compose compose;
  private TestComposeExecutor executor;
  private ComposeOptions options;

  @BeforeEach
  void setUp() {
    executor = new TestComposeExecutor();
    options = new ComposeOptions();
    options.file = List.of(DOCKER_COMPOSE_YML);
    compose = new Compose(executor);
    compose.options = options;
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("command line arguments with minimal settings")
  void dockerLikeMinimal(final String runtime) {
    final var tool = useRuntime(runtime);
    assertCommandLine(
      List.of(),
      List.of(),
      call(tool, "--file", DOCKER_COMPOSE_YML, "run", "-T"),
      call(tool, "--file", DOCKER_COMPOSE_YML, "down"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("command line arguments with default settings")
  void dockerLikeWithDefaults(final String runtime) {
    final var tool = useRuntime(runtime);
    options.interactive = true;
    options.service = "dev";
    assertCommandLine(
      List.of(),
      List.of(),
      call(tool, "--file", DOCKER_COMPOSE_YML, "run", options.service),
      call(tool, "--file", DOCKER_COMPOSE_YML, "down"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("command line arguments with pull")
  void dockerLikeWithPull(final String runtime) {
    final var tool = useRuntime(runtime);
    options.pull = true;
    assertCommandLine(
      call(tool, "--file", DOCKER_COMPOSE_YML, "pull"),
      List.of(),
      call(tool, "--file", DOCKER_COMPOSE_YML, "run", "-T"),
      call(tool, "--file", DOCKER_COMPOSE_YML, "down"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("command line arguments with build")
  void dockerLikeWithBuild(final String runtime) {
    final var tool = useRuntime(runtime);
    options.build = true;
    assertCommandLine(
      List.of(),
      call(tool, "--file", DOCKER_COMPOSE_YML, "build"),
      call(tool, "--file", DOCKER_COMPOSE_YML, "run", "-T"),
      call(tool, "--file", DOCKER_COMPOSE_YML, "down"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("command line arguments with runtime option")
  void dockerLikeWithRuntimeOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.runtimeOptions = List.of("--no-ansi");
    assertCommandLine(
      List.of(),
      List.of(),
      call(tool, "--no-ansi", "--file", DOCKER_COMPOSE_YML, "run", "-T"),
      call(tool, "--no-ansi", "--file", DOCKER_COMPOSE_YML, "down"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("command line arguments with runtime pull option")
  void dockerLikeWithRuntimePullOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.pull = true;
    options.runtimePullOptions = List.of("--parallel");
    assertCommandLine(
      call(tool, "--file", DOCKER_COMPOSE_YML, "pull", "--parallel"),
      List.of(),
      call(tool, "--file", DOCKER_COMPOSE_YML, "run", "-T"),
      call(tool, "--file", DOCKER_COMPOSE_YML, "down"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("command line arguments with runtime build option")
  void dockerLikeWithRuntimeBuildOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.build = true;
    options.runtimeBuildOptions = List.of("--no-cache");
    assertCommandLine(
      List.of(),
      call(tool, "--file", DOCKER_COMPOSE_YML, "build", "--no-cache"),
      call(tool, "--file", DOCKER_COMPOSE_YML, "run", "-T"),
      call(tool, "--file", DOCKER_COMPOSE_YML, "down"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("command line arguments with runtime run option")
  void dockerLikeWithRuntimeRunOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.runtimeRunOptions = List.of("--use-aliases");
    assertCommandLine(
      List.of(),
      List.of(),
      call(tool, "--file", DOCKER_COMPOSE_YML, "run", "--use-aliases", "-T"),
      call(tool, "--file", DOCKER_COMPOSE_YML, "down"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("command line arguments with runtime cleanup option")
  void dockerLikeWithRuntimeCleanupOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.runtimeCleanupOptions = List.of("--remove-orphans");
    assertCommandLine(
      List.of(),
      List.of(),
      call(tool, "--file", DOCKER_COMPOSE_YML, "run", "-T"),
      call(tool, "--file", DOCKER_COMPOSE_YML, "down", "--remove-orphans"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("fail early after pull")
  void failToPull(final String runtime) {
    final var tool = useRuntime(runtime);
    executor.exitCodes(1);
    options.pull = true;
    final var exitCode = compose.call();
    assertAll("command line",
      () -> assertEquals(1, exitCode, "exitCode"),
      () -> assertIterableEquals(call(tool, "--file", DOCKER_COMPOSE_YML, "pull"), executor.pullArguments(), "pullArguments"),
      () -> noExecution(executor::buildArguments, "buildArguments"),
      () -> noExecution(executor::runArguments, "runArguments"),
      () -> noExecution(executor::cleanupArguments, "cleanupArguments"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("fail early after build")
  void failToBuild(final String runtime) {
    final var tool = useRuntime(runtime);
    executor.exitCodes(0, 1);
    options.build = true;
    final var exitCode = compose.call();
    assertAll("command line",
      () -> assertEquals(1, exitCode, "exitCode"),
      () -> assertIterableEquals(List.of(), executor.pullArguments(), "pullArguments"),
      () -> assertIterableEquals(call(tool, "--file", DOCKER_COMPOSE_YML, "build"), executor.buildArguments(), "buildArguments"),
      () -> noExecution(executor::runArguments, "runArguments"),
      () -> noExecution(executor::cleanupArguments, "cleanupArguments"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("fail early after run")
  void failToRun(final String runtime) {
    final var tool = useRuntime(runtime);
    executor.exitCodes(0, 0, 1);
    final var exitCode = compose.call();
    assertAll("command line",
      () -> assertEquals(1, exitCode, "exitCode"),
      () -> assertIterableEquals(List.of(), executor.pullArguments(), "pullArguments"),
      () -> assertIterableEquals(List.of(), executor.buildArguments(), "buildArguments"),
      () -> assertIterableEquals(call(tool, "--file", DOCKER_COMPOSE_YML, "run", "-T"), executor.runArguments(), "runArguments"),
      () -> noExecution(executor::cleanupArguments, "cleanupArguments"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("return cleanup exit code")
  void failToClean(final String runtime) {
    final var tool = useRuntime(runtime);
    executor.exitCodes(0, 0, 0, 1);
    final var exitCode = compose.call();
    assertAll("command line",
      () -> assertEquals(1, exitCode, "exitCode"),
      () -> assertIterableEquals(List.of(), executor.pullArguments(), "pullArguments"),
      () -> assertIterableEquals(List.of(), executor.buildArguments(), "buildArguments"),
      () -> assertIterableEquals(call(tool, "--file", DOCKER_COMPOSE_YML, "run", "-T"), executor.runArguments(), "runArguments"),
      () -> assertIterableEquals(call(tool, "--file", DOCKER_COMPOSE_YML, "down"), executor.cleanupArguments(), "cleanupArguments"));
  }

  private String useRuntime(final String runtime) {
    options.runtime = ComposeRuntime.fromAlias(runtime);
    return options.runtime.aliases()[0];
  }

  private List<String> call(final String runtime, final String... args) {
    final var cli = "docker".equalsIgnoreCase(runtime)
      ? Stream.of(runtime, "compose")
      : Stream.of(runtime);
    return Stream.concat(cli, Arrays.stream(args)).toList();
  }

  private void assertCommandLine(
    final List<String> pullArguments,
    final List<String> buildArguments,
    final List<String> runArguments,
    final List<String> cleanupArguments) {
    final var exitCode = compose.call();
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
