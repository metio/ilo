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
import wtf.metio.ilo.shell.ShellRuntime;

@DisplayName("ilo shell")
class ShellTest extends CLI_TCK {

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("supports multiple runtimes")
  void defaultCommandLine(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool);
    Assertions.assertAll("shell options",
        () -> Assertions.assertNull(shell.options.commands),
        () -> Assertions.assertTrue(shell.options.interactive),
        () -> Assertions.assertTrue(shell.options.mountProjectDir),
        () -> Assertions.assertFalse(shell.options.debug),
        () -> Assertions.assertEquals("fedora:latest", shell.options.image),
        () -> Assertions.assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to disable mounting the project directory")
  void disableProjectDirMount(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--mount-project-dir=false");
    Assertions.assertAll("shell options",
        () -> Assertions.assertNull(shell.options.commands),
        () -> Assertions.assertTrue(shell.options.interactive),
        () -> Assertions.assertFalse(shell.options.mountProjectDir),
        () -> Assertions.assertFalse(shell.options.debug),
        () -> Assertions.assertEquals("fedora:latest", shell.options.image),
        () -> Assertions.assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to run non-interactive")
  void nonInteractive(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--interactive=false");
    Assertions.assertAll("shell options",
        () -> Assertions.assertNull(shell.options.commands),
        () -> Assertions.assertFalse(shell.options.interactive),
        () -> Assertions.assertTrue(shell.options.mountProjectDir),
        () -> Assertions.assertFalse(shell.options.debug),
        () -> Assertions.assertEquals("fedora:latest", shell.options.image),
        () -> Assertions.assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("has debug mode")
  void debug(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--debug");
    Assertions.assertAll("shell options",
        () -> Assertions.assertNull(shell.options.commands),
        () -> Assertions.assertTrue(shell.options.interactive),
        () -> Assertions.assertTrue(shell.options.mountProjectDir),
        () -> Assertions.assertTrue(shell.options.debug),
        () -> Assertions.assertEquals("fedora:latest", shell.options.image),
        () -> Assertions.assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to disable mounting and interactive")
  void nonInteractiveNonMounting(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--mount-project-dir=false", "--interactive=false");
    Assertions.assertAll("shell options",
        () -> Assertions.assertNull(shell.options.commands),
        () -> Assertions.assertFalse(shell.options.interactive),
        () -> Assertions.assertFalse(shell.options.mountProjectDir),
        () -> Assertions.assertFalse(shell.options.debug),
        () -> Assertions.assertEquals("fedora:latest", shell.options.image),
        () -> Assertions.assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify custom image")
  void customImage(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--image=example:test");
    Assertions.assertAll("shell options",
        () -> Assertions.assertNull(shell.options.commands),
        () -> Assertions.assertTrue(shell.options.interactive),
        () -> Assertions.assertTrue(shell.options.mountProjectDir),
        () -> Assertions.assertFalse(shell.options.debug),
        () -> Assertions.assertEquals("example:test", shell.options.image),
        () -> Assertions.assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

}
