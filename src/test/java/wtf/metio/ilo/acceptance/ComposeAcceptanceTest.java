/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wtf.metio.ilo.compose.ComposeRuntime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ilo compose")
class ComposeAcceptanceTest extends CLI_TCK {

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("supports multiple runtimes")
  void defaultCommandLine(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool);
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.pull, "pull"),
        () -> assertFalse(compose.options.build, "build"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertNull(compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("dev", compose.options.service, "service"),
        () -> assertNull(compose.options.arguments, "arguments")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allows to run non-interactive")
  void nonInteractive(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--interactive=false");
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertFalse(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.pull, "pull"),
        () -> assertFalse(compose.options.build, "build"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertNull(compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("dev", compose.options.service, "service"),
        () -> assertNull(compose.options.arguments, "arguments")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allows to run non-interactive with negated option")
  void interactiveNegated(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--no-interactive");
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertFalse(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.pull, "pull"),
        () -> assertFalse(compose.options.build, "build"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertNull(compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("dev", compose.options.service, "service"),
        () -> assertNull(compose.options.arguments, "arguments")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("has debug mode")
  void debug(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--debug");
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.pull, "pull"),
        () -> assertFalse(compose.options.build, "build"),
        () -> assertTrue(compose.options.debug, "debug"),
        () -> assertNull(compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("dev", compose.options.service, "service"),
        () -> assertNull(compose.options.arguments, "arguments")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("can pull images")
  void pull(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--pull");
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertTrue(compose.options.pull, "pull"),
        () -> assertFalse(compose.options.build, "build"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertNull(compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("dev", compose.options.service, "service"),
        () -> assertNull(compose.options.arguments, "arguments")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("can build images")
  void build(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--build");
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.pull, "pull"),
        () -> assertTrue(compose.options.build, "build"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertNull(compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("dev", compose.options.service, "service"),
        () -> assertNull(compose.options.arguments, "arguments")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allows to specify runtime options")
  void runtimeOptions(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--runtime-option=--no-ansi");
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.pull, "pull"),
        () -> assertFalse(compose.options.build, "build"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertIterableEquals(List.of("--no-ansi"), compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("dev", compose.options.service, "service"),
        () -> assertNull(compose.options.arguments, "arguments")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allows to specify runtime pull options")
  void runtimePullOptions(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--runtime-pull-option=--include-deps");
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.pull, "pull"),
        () -> assertFalse(compose.options.build, "build"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertNull(compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertIterableEquals(List.of("--include-deps"), compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("dev", compose.options.service, "service"),
        () -> assertNull(compose.options.arguments, "arguments")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allows to specify runtime build options")
  void runtimeBuildOptions(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--runtime-build-option=--no-cache");
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.pull, "pull"),
        () -> assertFalse(compose.options.build, "build"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertNull(compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertIterableEquals(List.of("--no-cache"), compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("dev", compose.options.service, "service"),
        () -> assertNull(compose.options.arguments, "arguments")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allows to specify runtime run options")
  void runtimeRunOptions(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--runtime-run-option=--no-deps");
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.pull, "pull"),
        () -> assertFalse(compose.options.build, "build"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertNull(compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertIterableEquals(List.of("--no-deps"), compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("dev", compose.options.service, "service"),
        () -> assertNull(compose.options.arguments, "arguments")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allows to specify runtime cleanup options")
  void runtimeCleanupOptions(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--runtime-cleanup-option=--remove-orphans");
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.pull, "pull"),
        () -> assertFalse(compose.options.build, "build"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertNull(compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertIterableEquals(List.of("--remove-orphans"), compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("dev", compose.options.service, "service"),
        () -> assertNull(compose.options.arguments, "arguments")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allows to specify service to run")
  void service(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "your-service");
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.pull, "pull"),
        () -> assertFalse(compose.options.build, "build"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertNull(compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("your-service", compose.options.service, "service"),
        () -> assertNull(compose.options.arguments, "arguments")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allows to specify run arguments")
  void arguments(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "your-service", "/bin/bash");
    assertAll("compose options",
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime"),
        () -> assertIterableEquals(List.of("docker-compose.yml"), compose.options.file, "file"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.pull, "pull"),
        () -> assertFalse(compose.options.build, "build"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertNull(compose.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(compose.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(compose.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(compose.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(compose.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("your-service", compose.options.service, "service"),
        () -> assertIterableEquals(List.of("/bin/bash"), compose.options.arguments, "arguments")
    );
  }

}
