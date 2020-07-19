/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wtf.metio.ilo.model.Runtime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ilo")
class IloTest extends CLI_TCK {

  @Test
  @DisplayName("select runtime automatically by default")
  void shouldDefaultToAutoRuntimeSelection() {
    final var shell = shell("shell");
    assertNull(shell.options.runtime);
  }

  @DisplayName("allow to specify runtime")
  @ParameterizedTest
  @ValueSource(strings = {"podman", "docker", "p", "d"})
  void shouldAllowToSpecifyRuntime(final String runtime) {
    final var shell = shell("shell", "--runtime", runtime);
    assertEquals(Runtime.fromAlias(runtime), shell.options.runtime);
  }

  @Test
  @DisplayName("default image is 'fedora:latest'")
  void shouldDefaultToLatestFedoraImage() {
    final var shell = shell("shell");
    assertEquals("fedora:latest", shell.options.image);
  }

  @Test
  @DisplayName("custom images are supported")
  void shouldAllowToSpecifyCustomImage() {
    final var shell = shell("shell", "--image", "my.own.image:awesome");
    assertEquals("my.own.image:awesome", shell.options.image);
  }

  @Test
  @DisplayName("debug is disabled by default")
  void shouldDisableDebugByDefault() {
    final var shell = shell("shell");
    assertFalse(shell.options.debug);
  }

  @Test
  @DisplayName("allow to enable debug")
  void shouldAllowToEnableDebug() {
    final var shell = shell("shell", "--debug");
    assertTrue(shell.options.debug);
  }

  @Test
  @DisplayName("allow to disable debug")
  void shouldAllowToDisableDebug() {
    final var shell = shell("shell", "--debug=false");
    assertFalse(shell.options.debug);
  }

  @Test
  @DisplayName("interactive mode is enabled by default")
  void shouldEnableInteractiveModeByDefault() {
    final var shell = shell("shell");
    assertTrue(shell.options.interactive);
  }

  @Test
  @Disabled("negate does not work from tests?")
  @DisplayName("interactive mode can be negated")
  void shouldAllowToNegateInteractiveMode() {
    final var shell = shell("shell", "--no-interactive");
    assertFalse(shell.options.interactive);
  }

  @Test
  @DisplayName("interactive mode can be disabled")
  void shouldAllowToDisableInteractiveMode() {
    final var shell = shell("shell", "--interactive=false");
    assertFalse(shell.options.interactive);
  }

  @Test
  @DisplayName("project directory should be mounted by default")
  void shouldMountProjectDirectoryByDefault() {
    final var shell = shell("shell");
    assertTrue(shell.options.mountProjectDir);
  }

  @Test
  @DisplayName("mounting the project directory can be disabled")
  void shouldAllowToDisableProjectDirectoryMounting() {
    final var shell = shell("shell", "--mount-project-dir=false");
    assertFalse(shell.options.mountProjectDir);
  }

}
