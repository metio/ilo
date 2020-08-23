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
import wtf.metio.ilo.shell.ShellRuntime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ilo shell")
class ShellTest extends CLI_TCK {

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("supports multiple runtimes")
  void defaultCommandLine(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool);
    assertAll("shell options",
        () -> assertNull(shell.options.commands),
        () -> assertTrue(shell.options.interactive),
        () -> assertTrue(shell.options.mountProjectDir),
        () -> assertFalse(shell.options.debug),
        () -> assertFalse(shell.options.removeImage),
        () -> assertEquals("fedora:latest", shell.options.image),
        () -> assertNull(shell.options.dockerfile),
        () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to disable mounting the project directory")
  void disableProjectDirMount(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--mount-project-dir=false");
    assertAll("shell options",
        () -> assertNull(shell.options.commands),
        () -> assertTrue(shell.options.interactive),
        () -> assertFalse(shell.options.mountProjectDir),
        () -> assertFalse(shell.options.debug),
        () -> assertFalse(shell.options.removeImage),
        () -> assertEquals("fedora:latest", shell.options.image),
        () -> assertNull(shell.options.dockerfile),
        () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to run non-interactive")
  void nonInteractive(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--interactive=false");
    assertAll("shell options",
        () -> assertNull(shell.options.commands),
        () -> assertFalse(shell.options.interactive),
        () -> assertTrue(shell.options.mountProjectDir),
        () -> assertFalse(shell.options.debug),
        () -> assertFalse(shell.options.removeImage),
        () -> assertEquals("fedora:latest", shell.options.image),
        () -> assertNull(shell.options.dockerfile),
        () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("has debug mode")
  void debug(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--debug");
    assertAll("shell options",
        () -> assertNull(shell.options.commands),
        () -> assertTrue(shell.options.interactive),
        () -> assertTrue(shell.options.mountProjectDir),
        () -> assertTrue(shell.options.debug),
        () -> assertFalse(shell.options.removeImage),
        () -> assertEquals("fedora:latest", shell.options.image),
        () -> assertNull(shell.options.dockerfile),
        () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to disable mounting and interactive")
  void nonInteractiveNonMounting(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--mount-project-dir=false", "--interactive=false");
    assertAll("shell options",
        () -> assertNull(shell.options.commands),
        () -> assertFalse(shell.options.interactive),
        () -> assertFalse(shell.options.mountProjectDir),
        () -> assertFalse(shell.options.debug),
        () -> assertFalse(shell.options.removeImage),
        () -> assertEquals("fedora:latest", shell.options.image),
        () -> assertNull(shell.options.dockerfile),
        () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify custom image")
  void customImage(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--image=example:test");
    assertAll("shell options",
        () -> assertNull(shell.options.commands),
        () -> assertTrue(shell.options.interactive),
        () -> assertTrue(shell.options.mountProjectDir),
        () -> assertFalse(shell.options.debug),
        () -> assertFalse(shell.options.removeImage),
        () -> assertEquals("example:test", shell.options.image),
        () -> assertNull(shell.options.dockerfile),
        () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify dockerfile")
  void dockerfile(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--dockerfile=build/env.dockerfile");
    assertAll("shell options",
        () -> assertNull(shell.options.commands),
        () -> assertTrue(shell.options.interactive),
        () -> assertTrue(shell.options.mountProjectDir),
        () -> assertFalse(shell.options.debug),
        () -> assertFalse(shell.options.removeImage),
        () -> assertEquals("fedora:latest", shell.options.image),
        () -> assertEquals("build/env.dockerfile", shell.options.dockerfile),
        () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

}
