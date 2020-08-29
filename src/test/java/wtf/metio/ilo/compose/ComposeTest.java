/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wtf.metio.ilo.test.TestMethodSources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DisplayName("Compose")
class ComposeTest extends TestMethodSources {

  private Compose compose;
  private TestComposeExecutor executor;
  private ComposeOptions options;

  @BeforeEach
  void setUp() {
    executor = new TestComposeExecutor();
    options = new ComposeOptions();
    options.file = "docker-compose.yml";
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
        List.of(tool, "--file", options.file, "run", "-T"),
        List.of(tool, "--file", options.file, "down"));
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
        List.of(tool, "--file", options.file, "run", options.service),
        List.of(tool, "--file", options.file, "down"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("command line arguments with pull")
  void dockerLikeWithPull(final String runtime) {
    final var tool = useRuntime(runtime);
    options.pull = true;
    assertCommandLine(
        List.of(tool, "--file", options.file, "pull"),
        List.of(),
        List.of(tool, "--file", options.file, "run", "-T"),
        List.of(tool, "--file", options.file, "down"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("command line arguments with build")
  void dockerLikeWithBuild(final String runtime) {
    final var tool = useRuntime(runtime);
    options.build = true;
    assertCommandLine(
        List.of(),
        List.of(tool, "--file", options.file, "build"),
        List.of(tool, "--file", options.file, "run", "-T"),
        List.of(tool, "--file", options.file, "down"));
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
        List.of(tool, "--no-ansi", "--file", options.file, "run", "-T"),
        List.of(tool, "--no-ansi", "--file", options.file, "down"));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("command line arguments with runtime pull option")
  void dockerLikeWithRuntimePullOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.pull = true;
    options.runtimePullOptions = List.of("--parallel");
    assertCommandLine(
        List.of(tool, "--file", options.file, "pull", "--parallel"),
        List.of(),
        List.of(tool, "--file", options.file, "run", "-T"),
        List.of(tool, "--file", options.file, "down"));
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
        List.of(tool, "--file", options.file, "build", "--no-cache"),
        List.of(tool, "--file", options.file, "run", "-T"),
        List.of(tool, "--file", options.file, "down"));
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
        List.of(tool, "--file", options.file, "run", "--use-aliases", "-T"),
        List.of(tool, "--file", options.file, "down"));
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
        List.of(tool, "--file", options.file, "run", "-T"),
        List.of(tool, "--file", options.file, "down", "--remove-orphans"));
  }

  private String useRuntime(final String runtime) {
    options.runtime = ComposeRuntime.fromAlias(runtime);
    return options.runtime.aliases()[0];
  }

  private void assertCommandLine(
      final List<String> pullArguments,
      final List<String> buildArguments,
      final List<String> runArguments,
      final List<String> cleanupArguments) {
    compose.call();
    assertAll("command line arguments",
        () -> assertIterableEquals(pullArguments, executor.pullArguments(), "pullArguments"),
        () -> assertIterableEquals(buildArguments, executor.buildArguments(), "buildArguments"),
        () -> assertIterableEquals(runArguments, executor.runArguments(), "runArguments"),
        () -> assertIterableEquals(cleanupArguments, executor.cleanupArguments(), "cleanupArguments"));
  }

}