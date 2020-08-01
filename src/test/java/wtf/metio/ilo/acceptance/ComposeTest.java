/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.acceptance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wtf.metio.ilo.compose.ComposeRuntime;

@DisplayName("ilo compose")
class ComposeTest extends CLI_TCK {

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("supports multiple runtimes")
  void defaultCommandLine(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool);
    Assertions.assertAll("compose options",
        () -> Assertions.assertEquals("docker-compose.yml", compose.options.file),
        () -> Assertions.assertNull(compose.options.service),
        () -> Assertions.assertTrue(compose.options.interactive),
        () -> Assertions.assertFalse(compose.options.debug),
        () -> Assertions.assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("allow to run non-interactive")
  void nonInteractive(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--interactive=false");
    Assertions.assertAll("compose options",
        () -> Assertions.assertEquals("docker-compose.yml", compose.options.file),
        () -> Assertions.assertNull(compose.options.service),
        () -> Assertions.assertFalse(compose.options.interactive),
        () -> Assertions.assertFalse(compose.options.debug),
        () -> Assertions.assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("has debug mode")
  void debug(final String tool) {
    final var compose = parseComposeCommand("compose", "--runtime", tool, "--debug");
    Assertions.assertAll("compose options",
        () -> Assertions.assertEquals("docker-compose.yml", compose.options.file),
        () -> Assertions.assertNull(compose.options.service),
        () -> Assertions.assertTrue(compose.options.interactive),
        () -> Assertions.assertTrue(compose.options.debug),
        () -> Assertions.assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime)
    );
  }

}
