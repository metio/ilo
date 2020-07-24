/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;

@DisplayName("ilo shell")
class ShellTest extends CLI_TCK {
//
//  @ParameterizedTest
//  @DisplayName("calls command line tool")
//  @ValueSource(strings = {"podman", "docker", "p", "d"})
//  void defaultCommandLine(final String tool) {
//    final var shell = shell("shell", "--runtime", tool);
//    final var arguments = shellArguments(shell.options, tool);
//    final var cmd = List.of(tool,
//        "run", "--rm",
//        "--volume", "--workdir",
//        "--interactive", "--tty");
//    assertCommandLine(cmd, arguments);
//  }
//
//  @ParameterizedTest
//  @DisplayName("allow to disable mounting the project directory")
//  @ValueSource(strings = {"podman", "docker", "p", "d"})
//  void disableProjectDirMount(final String tool) {
//    final var shell = shell("shell", "--runtime", tool, "--mount-project-dir=false");
//    final var arguments = shellArguments(shell.options, tool);
//    final var cmd = List.of(tool,
//        "run", "--rm",
//        "--interactive", "--tty");
//    assertCommandLine(cmd, arguments);
//  }
//
//  @ParameterizedTest
//  @DisplayName("allow to run non-interactive")
//  @ValueSource(strings = {"podman", "docker", "p", "d"})
//  void nonInteractive(final String tool) {
//    final var shell = shell("shell", "--runtime", tool, "--interactive=false");
//    final var arguments = shellArguments(shell.options, tool);
//    final var cmd = List.of(tool,
//        "run", "--rm",
//        "--volume", "--workdir");
//    assertCommandLine(cmd, arguments);
//  }
//
//  @ParameterizedTest
//  @DisplayName("debug mode does not influence command line")
//  @ValueSource(strings = {"podman", "docker", "p", "d"})
//  void debugDoesNotChangeCmd(final String tool) {
//    final var shell = shell("shell", "--runtime", tool, "--debug");
//    final var arguments = shellArguments(shell.options, tool);
//    final var cmd = List.of(tool,
//        "run", "--rm",
//        "--volume", "--workdir",
//        "--interactive", "--tty");
//    assertCommandLine(cmd, arguments);
//  }
//
//  @ParameterizedTest
//  @DisplayName("allow to run non-interactive w/o mounting")
//  @ValueSource(strings = {"podman", "docker", "p", "d"})
//  void nonInteractiveDisableMounting(final String tool) {
//    final var shell = shell("shell", "--runtime", tool, "--interactive=false", "--mount-project-dir=false");
//    final var arguments = shellArguments(shell.options, tool);
//    final var cmd = List.of(tool, "run", "--rm");
//    assertCommandLine(cmd, arguments);
//  }

  // TODO: ensure that we can still run fedora:latest as default image w/o any additional parameters

}
