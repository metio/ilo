/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wtf.metio.ilo.test.TestMethodSources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DisplayName("Shell")
class ShellTest extends TestMethodSources {

  private Shell shell;
  private TestShellExecutor executor;
  private ShellOptions options;

  @BeforeEach
  void setUp() {
    executor = new TestShellExecutor();
    options = new ShellOptions();
    options.image = "fedora:latest";
    shell = new Shell(executor);
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
        List.of(tool, "run", "--rm", options.image),
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
        List.of(tool, "run", "--rm", options.image),
        List.of());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with build")
  void dockerLikeWithBuild(final String runtime) {
    final var tool = useRuntime(runtime);
    options.dockerfile = "Dockerfile";
    assertCommandLine(
        List.of(),
        List.of(tool, "build", "--file", options.dockerfile, "--tag", options.image, "."),
        List.of(tool, "run", "--rm", options.image),
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
        List.of(tool, "run", "--rm", options.image),
        List.of(tool, "rmi", options.image));
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with default settings")
  void dockerLikeWithDefaults(final String runtime) {
    final var tool = useRuntime(runtime);
    options.interactive = true;
    options.mountProjectDir = true;
    System.setProperty("user.dir", "/some/folder");
    assertCommandLine(
        List.of(),
        List.of(),
        List.of(tool, "run", "--rm", "--volume", "/some/folder:/some/folder:Z", "--workdir", "/some/folder", "--interactive", "--tty", options.image),
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
        List.of(tool, "--remote", "run", "--rm", options.image),
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
        List.of(tool, "run", "--rm", options.image),
        List.of());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("command line arguments with runtime build option")
  void dockerLikeWithRuntimeBuildOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.dockerfile = "Dockerfile";
    options.runtimeBuildOptions = List.of("--squash-all");
    assertCommandLine(
        List.of(),
        List.of(tool, "build", "--file", options.dockerfile, "--squash-all", "--tag", options.image, "."),
        List.of(tool, "run", "--rm", options.image),
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
        List.of(tool, "run", "--rm", "--quiet", options.image),
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
        List.of(tool, "run", "--rm", options.image),
        List.of(tool, "rmi", "--force", options.image));
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
    shell.call();
    assertAll("command line arguments",
        () -> assertIterableEquals(pullArguments, executor.pullArguments(), "pullArguments"),
        () -> assertIterableEquals(buildArguments, executor.buildArguments(), "buildArguments"),
        () -> assertIterableEquals(runArguments, executor.runArguments(), "runArguments"),
        () -> assertIterableEquals(cleanupArguments, executor.cleanupArguments(), "cleanupArguments"));
  }

}