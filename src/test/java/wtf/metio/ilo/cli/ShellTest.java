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
import wtf.metio.ilo.shell.ShellRuntime;

@DisplayName("ilo shell")
class ShellTest extends CLI_TCK {

  @ParameterizedTest
  @DisplayName("supports multiple runtimes")
  @ValueSource(strings = {"podman", "docker", "p", "d"})
  void defaultCommandLine(final String tool) {
    final var shell = shell("shell", "--runtime", tool);
    Assertions.assertAll("shell options",
        () -> Assertions.assertNull(shell.options.commands),
        () -> Assertions.assertTrue(shell.options.interactive),
        () -> Assertions.assertTrue(shell.options.mountProjectDir),
        () -> Assertions.assertFalse(shell.options.debug),
        () -> Assertions.assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @DisplayName("allows to disable mounting the project directory")
  @ValueSource(strings = {"podman", "docker", "p", "d"})
  void disableProjectDirMount(final String tool) {
    final var shell = shell("shell", "--runtime", tool, "--mount-project-dir=false");
    Assertions.assertAll("shell options",
        () -> Assertions.assertNull(shell.options.commands),
        () -> Assertions.assertTrue(shell.options.interactive),
        () -> Assertions.assertFalse(shell.options.mountProjectDir),
        () -> Assertions.assertFalse(shell.options.debug),
        () -> Assertions.assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @DisplayName("allows to run non-interactive")
  @ValueSource(strings = {"podman", "docker", "p", "d"})
  void nonInteractive(final String tool) {
    final var shell = shell("shell", "--runtime", tool, "--interactive=false");
    Assertions.assertAll("shell options",
        () -> Assertions.assertNull(shell.options.commands),
        () -> Assertions.assertFalse(shell.options.interactive),
        () -> Assertions.assertTrue(shell.options.mountProjectDir),
        () -> Assertions.assertFalse(shell.options.debug),
        () -> Assertions.assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @DisplayName("has debug mode")
  @ValueSource(strings = {"podman", "docker", "p", "d"})
  void debug(final String tool) {
    final var shell = shell("shell", "--runtime", tool, "--debug");
    Assertions.assertAll("shell options",
        () -> Assertions.assertNull(shell.options.commands),
        () -> Assertions.assertTrue(shell.options.interactive),
        () -> Assertions.assertTrue(shell.options.mountProjectDir),
        () -> Assertions.assertTrue(shell.options.debug),
        () -> Assertions.assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  @ParameterizedTest
  @DisplayName("allows to disable mounting and interactive")
  @ValueSource(strings = {"podman", "docker", "p", "d"})
  void nonInteractiveNonMounting(final String tool) {
    final var shell = shell("shell", "--runtime", tool, "--mount-project-dir=false", "--interactive=false");
    Assertions.assertAll("shell options",
        () -> Assertions.assertNull(shell.options.commands),
        () -> Assertions.assertFalse(shell.options.interactive),
        () -> Assertions.assertFalse(shell.options.mountProjectDir),
        () -> Assertions.assertFalse(shell.options.debug),
        () -> Assertions.assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime)
    );
  }

  // TODO: ensure that we can still run fedora:latest as default image w/o any additional parameters

}
