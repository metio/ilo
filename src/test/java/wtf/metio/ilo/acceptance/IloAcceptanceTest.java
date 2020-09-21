/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.acceptance;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wtf.metio.ilo.shell.ShellRuntime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ilo")
class IloAcceptanceTest extends CLI_TCK {

  @Test
  @DisplayName("select runtime automatically by default")
  void shouldDefaultToAutoRuntimeSelection() {
    final var shell = parseShellCommand("shell");
    assertNull(shell.options.runtime);
  }

  @DisplayName("allow to specify runtime")
  @ParameterizedTest
  @ValueSource(strings = {"podman", "docker", "p", "d"})
  void shouldAllowToSpecifyRuntime(final String runtime) {
    final var shell = parseShellCommand("shell", "--runtime", runtime);
    assertEquals(ShellRuntime.fromAlias(runtime), shell.options.runtime);
  }

  @Test
  @DisplayName("debug is disabled by default")
  void shouldDisableDebugByDefault() {
    final var shell = parseShellCommand("shell");
    assertFalse(shell.options.debug);
  }

  @Test
  @DisplayName("allow to enable debug")
  void shouldAllowToEnableDebug() {
    final var shell = parseShellCommand("shell", "--debug");
    assertTrue(shell.options.debug);
  }

  @Test
  @DisplayName("allow to disable debug")
  void shouldAllowToDisableDebug() {
    final var shell = parseShellCommand("shell", "--debug=false");
    assertFalse(shell.options.debug);
  }

  @Test
  @DisplayName("interactive mode is enabled by default")
  void shouldEnableInteractiveModeByDefault() {
    final var shell = parseShellCommand("shell");
    assertTrue(shell.options.interactive);
  }

  @Test
  @Disabled("negate does not work from tests?")
  @DisplayName("interactive mode can be negated")
  void shouldAllowToNegateInteractiveMode() {
    final var shell = parseShellCommand("shell", "--no-interactive");
    assertFalse(shell.options.interactive);
  }

  @Test
  @DisplayName("interactive mode can be disabled")
  void shouldAllowToDisableInteractiveMode() {
    final var shell = parseShellCommand("shell", "--interactive=false");
    assertFalse(shell.options.interactive);
  }

  @Test
  @DisplayName("project directory should be mounted by default")
  void shouldMountProjectDirectoryByDefault() {
    final var shell = parseShellCommand("shell");
    assertTrue(shell.options.mountProjectDir);
  }

  @Test
  @DisplayName("mounting the project directory can be disabled")
  void shouldAllowToDisableProjectDirectoryMounting() {
    final var shell = parseShellCommand("shell", "--mount-project-dir=false");
    assertFalse(shell.options.mountProjectDir);
  }

}
