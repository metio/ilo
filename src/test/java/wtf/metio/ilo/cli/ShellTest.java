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

import static wtf.metio.ilo.utils.CalculateArguments.shellArguments;

class ShellTest extends CLI_TCK {

  @DisplayName("create shell command line")
  @ParameterizedTest
  @ValueSource(strings = {"podman", "docker"})
  void shouldCreateShellCommandLine(final String tool) {
    final var shell = shell("shell", "--runtime", tool);
    final var arguments = shellArguments(shell.options, tool);
    Assertions.assertAll("shell command",
        () -> Assertions.assertTrue(arguments.contains(tool)),
        () -> Assertions.assertTrue(arguments.contains("run")),
        () -> Assertions.assertTrue(arguments.contains("--rm")),
        () -> Assertions.assertTrue(arguments.contains("--volume")),
        () -> Assertions.assertTrue(arguments.contains("--workdir")),
        () -> Assertions.assertTrue(arguments.contains("--interactive")),
        () -> Assertions.assertTrue(arguments.contains("--tty")),
        () -> Assertions.assertTrue(arguments.contains("fedora:latest")),
        () -> Assertions.assertTrue(arguments.contains("/bin/bash"))
    );
  }

}
