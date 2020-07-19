/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static wtf.metio.ilo.utils.CalculateArguments.shellArguments;

class ShellTest extends CLI_TCK {

  @DisplayName("create shell command line")
  @ParameterizedTest
  @ValueSource(strings = {"podman", "docker"})
  void shouldCreateShellCommandLine(final String tool) {
    final var shell = shell("shell", "--runtime", tool);
    final var arguments = shellArguments(shell.options, tool);
    final var cmd = List.of(tool,
        "run", "--rm",
        "--volume", "--workdir",
        "--interactive", "--tty",
        "fedora:latest", "/bin/bash");
    assertCommandLine(cmd, arguments);
  }

}
