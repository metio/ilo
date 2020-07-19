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

@DisplayName("ilo shell")
class ShellTest extends CLI_TCK {

  @ParameterizedTest
  @DisplayName("calls command line tool")
  @ValueSource(strings = {"podman", "docker"})
  void defaultCommandLine(final String tool) {
    final var shell = shell("shell", "--runtime", tool);
    final var arguments = shellArguments(shell.options, tool);
    final var cmd = List.of(tool,
        "run", "--rm",
        "--volume", "--workdir",
        "--interactive", "--tty",
        "fedora:latest", "/bin/bash");
    assertCommandLine(cmd, arguments);
  }

  @ParameterizedTest
  @DisplayName("allow to disable mounting the project directory")
  @ValueSource(strings = {"podman", "docker"})
  void disableProjectDirMount(final String tool) {
    final var shell = shell("shell", "--runtime", tool, "--mount-project-dir=false");
    final var arguments = shellArguments(shell.options, tool);
    final var cmd = List.of(tool,
        "run", "--rm",
        "--interactive", "--tty",
        "fedora:latest", "/bin/bash");
    assertCommandLine(cmd, arguments);
  }

  @ParameterizedTest
  @DisplayName("allow to run non-interactive")
  @ValueSource(strings = {"podman", "docker"})
  void nonInteractive(final String tool) {
    final var shell = shell("shell", "--runtime", tool, "--interactive=false");
    final var arguments = shellArguments(shell.options, tool);
    final var cmd = List.of(tool,
        "run", "--rm",
        "--volume", "--workdir",
        "fedora:latest", "/bin/bash");
    assertCommandLine(cmd, arguments);
  }

  @ParameterizedTest
  @DisplayName("debug mode does not influence command line")
  @ValueSource(strings = {"podman", "docker"})
  void debugDoesNotChangeCmd(final String tool) {
    final var shell = shell("shell", "--runtime", tool, "--debug");
    final var arguments = shellArguments(shell.options, tool);
    final var cmd = List.of(tool,
        "run", "--rm",
        "--volume", "--workdir",
        "--interactive", "--tty",
        "fedora:latest", "/bin/bash");
    assertCommandLine(cmd, arguments);
  }

  @ParameterizedTest
  @DisplayName("allow to run non-interactive w/o mounting")
  @ValueSource(strings = {"podman", "docker"})
  void nonInteractiveDisableMounting(final String tool) {
    final var shell = shell("shell", "--runtime", tool, "--interactive=false", "--mount-project-dir=false");
    final var arguments = shellArguments(shell.options, tool);
    final var cmd = List.of(tool,
        "run", "--rm",
        "fedora:latest", "/bin/bash");
    assertCommandLine(cmd, arguments);
  }

}
