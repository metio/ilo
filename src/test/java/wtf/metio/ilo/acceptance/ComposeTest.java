/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wtf.metio.ilo.compose.ComposeRuntime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ilo compose")
class ComposeTest extends CLI_TCK {

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("supports multiple runtimes")
  void defaultCommandLine(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool);
    assertAll("compose options",
        () -> assertEquals("docker-compose.yml", compose.options.file, "file"),
        () -> assertNull(compose.options.service, "service"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allow to run interactive")
  void interactive(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--interactive");
    assertAll("compose options",
        () -> assertEquals("docker-compose.yml", compose.options.file, "file"),
        () -> assertNull(compose.options.service, "service"),
        () -> assertTrue(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allow to run non-interactive")
  void nonInteractive(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--interactive=false");
    assertAll("compose options",
        () -> assertEquals("docker-compose.yml", compose.options.file, "file"),
        () -> assertNull(compose.options.service, "service"),
        () -> assertFalse(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allow to run non-interactive with negated option")
  void interactiveNegated(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--no-interactive");
    assertAll("compose options",
        () -> assertEquals("docker-compose.yml", compose.options.file, "file"),
        () -> assertNull(compose.options.service, "service"),
        () -> assertFalse(compose.options.interactive, "interactive"),
        () -> assertFalse(compose.options.debug, "debug"),
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime, "runtime")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("has debug mode")
  void debug(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--debug");
    assertAll("compose options",
        () -> assertEquals("docker-compose.yml", compose.options.file),
        () -> assertNull(compose.options.service),
        () -> assertTrue(compose.options.interactive),
        () -> assertTrue(compose.options.debug),
        () -> assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime)
    );
  }

}
