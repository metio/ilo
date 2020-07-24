/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wtf.metio.ilo.compose.ComposeRuntime;

@DisplayName("ilo compose")
class ComposeTest extends CLI_TCK {

  @ParameterizedTest
  @DisplayName("calls command line tool")
  @ValueSource(strings = {
      "podman-compose",
      "docker-compose",
      "pods-compose",
      "pc",
      "dc",
      "pods"
  })
  void defaultCommandLine(final String tool) {
    final var compose = compose("compose", "--runtime", tool);
    Assertions.assertAll("command line",
        () -> Assertions.assertEquals("docker-compose.yml", compose.options.composeFile),
        () -> Assertions.assertNull(compose.options.service),
        () -> Assertions.assertTrue(compose.options.interactive),
        () -> Assertions.assertFalse(compose.options.debug),
        () -> Assertions.assertEquals(ComposeRuntime.fromAlias(tool), compose.options.runtime)
    );
  }

//  @ParameterizedTest
//  @DisplayName("allow to run non-interactive")
//  @ValueSource(strings = {"podman-compose", "docker-compose", "pc", "dc"})
//  void nonInteractive(final String tool) {
//    final var compose = compose("compose", "--runtime", tool, "--interactive=false");
//    final var arguments = composeRunArguments(compose.options, tool);
//    final var cmd = List.of(tool,
//        "run", "--rm",
//        "--file", "docker-compose.yml");
//    assertCommandLine(cmd, arguments);
//  }
//
//  @ParameterizedTest
//  @DisplayName("debug mode does not influence command line")
//  @ValueSource(strings = {"podman-compose", "docker-compose", "pc", "dc"})
//  void debugDoesNotChangeCmd(final String tool) {
//    final var compose = compose("compose", "--runtime", tool, "--debug");
//    final var arguments = composeRunArguments(compose.options, tool);
//    final var cmd = List.of(tool,
//        "run", "--rm",
//        "--file", "docker-compose.yml");
//    assertCommandLine(cmd, arguments);
//  }

}
