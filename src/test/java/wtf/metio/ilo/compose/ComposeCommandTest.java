/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.compose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import wtf.metio.ilo.test.TestMethodSources;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DisplayName("ComposeCommand")
@ExtendWith(SystemStubsExtension.class)
class ComposeCommandTest extends TestMethodSources {

  private static final String YML = "docker-compose.yml";

  private ComposeCommand compose;
  private TestComposeExecutor executor;
  private ComposeOptions options;

  @BeforeEach
  void setUp() {
    executor = new TestComposeExecutor();
    options = new ComposeOptions();
    options.file = List.of(YML);
    options.service = "dev";
    compose = new ComposeCommand(executor);
    compose.options = options;
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("brings up the services and execs into them")
  void minimal(final String runtime) {
    final var tool = useRuntime(runtime);
    final var exitCode = compose.call();
    assertEquals(0, exitCode);
    assertIterableEquals(List.of(
        call(tool, "--file", YML, "up", "--detach", "dev"),
        call(tool, "--file", YML, "exec", "-T", "dev", "/bin/sh"),
        call(tool, "--file", YML, "stop")), executor.executed());
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("pulls before bringing up the services")
  void withPull(final String runtime) {
    final var tool = useRuntime(runtime);
    options.pull = true;
    compose.call();
    assertIterableEquals(List.of(
        call(tool, "--file", YML, "pull"),
        call(tool, "--file", YML, "up", "--detach", "dev"),
        call(tool, "--file", YML, "exec", "-T", "dev", "/bin/sh"),
        call(tool, "--file", YML, "stop")), executor.executed());
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("builds before bringing up the services")
  void withBuild(final String runtime) {
    final var tool = useRuntime(runtime);
    options.build = true;
    compose.call();
    assertIterableEquals(List.of(
        call(tool, "--file", YML, "build"),
        call(tool, "--file", YML, "up", "--detach", "dev"),
        call(tool, "--file", YML, "exec", "-T", "dev", "/bin/sh"),
        call(tool, "--file", YML, "stop")), executor.executed());
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("tears the services down before recreating them when fresh")
  void fresh(final String runtime) {
    final var tool = useRuntime(runtime);
    options.fresh = true;
    compose.call();
    assertIterableEquals(List.of(
        call(tool, "--file", YML, "down"),
        call(tool, "--file", YML, "up", "--detach", "dev"),
        call(tool, "--file", YML, "exec", "-T", "dev", "/bin/sh"),
        call(tool, "--file", YML, "stop")), executor.executed());
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("execs a custom command instead of the shell")
  void customArguments(final String runtime) {
    final var tool = useRuntime(runtime);
    options.arguments = List.of("ls", "-la");
    compose.call();
    assertIterableEquals(
        call(tool, "--file", YML, "exec", "-T", "dev", "ls", "-la"),
        executor.executed().get(1));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("passes runtime run options to the up step")
  void withRuntimeRunOption(final String runtime) {
    final var tool = useRuntime(runtime);
    options.runtimeRunOptions = List.of("--use-aliases");
    compose.call();
    assertIterableEquals(
        call(tool, "--file", YML, "up", "--detach", "--use-aliases", "dev"),
        executor.executed().get(0));
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("reports the exit code of the attached command")
  void reportsAttachExitCode(final String runtime) {
    useRuntime(runtime);
    // pull (empty), build (empty), up, exec — the fourth executed step is the attach.
    executor.exitCodes(0, 0, 0, 7);
    assertEquals(7, compose.call());
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("does not bring up the services when the build fails")
  void shortCircuitsOnBuildFailure(final String runtime) {
    final var tool = useRuntime(runtime);
    options.build = true;
    // The empty pull step consumes the first exit code; the build then fails.
    executor.exitCodes(0, 1);
    final var exitCode = compose.call();
    assertEquals(1, exitCode);
    assertIterableEquals(List.of(call(tool, "--file", YML, "build")), executor.executed());
  }

  @ParameterizedTest
  @MethodSource("dockerComposeLikeRuntimes")
  @DisplayName("leaves the services running when asked to keep them")
  void keepRunning(final String runtime) {
    final var tool = useRuntime(runtime);
    options.keepRunning = true;
    compose.call();
    assertIterableEquals(List.of(
        call(tool, "--file", YML, "up", "--detach", "dev"),
        call(tool, "--file", YML, "exec", "-T", "dev", "/bin/sh")), executor.executed());
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

}
