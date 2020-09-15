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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ilo shell")
class ShellTest extends CLI_TCK {

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("supports multiple runtimes")
  void defaultCommandLine(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool);
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to disable mounting the project directory")
  void disableProjectDirMount(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--mount-project-dir=false");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertFalse(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to disable mounting the project directory")
  void negateProjectDirMount(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--no-mount-project-dir");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertFalse(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to run non-interactive")
  void nonInteractive(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--interactive=false");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertFalse(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to run non-interactive")
  void interactiveNegated(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--no-interactive");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertFalse(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("has debug mode")
  void debug(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--debug");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertTrue(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to disable mounting and interactive")
  void nonInteractiveNonMounting(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--no-mount-project-dir", "--no-interactive");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertFalse(shell.options.interactive, "interactive"),
      () -> assertFalse(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify custom image")
  void customImage(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "example:test");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("example:test", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify dockerfile")
  void dockerfile(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--dockerfile=build/env.dockerfile");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertEquals("build/env.dockerfile", shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to custom commands")
  void customCommands(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "example:123", "/bin/bash", "-c", "cp /a /b;jshell");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("example:123", shell.options.image, "image"),
      () -> assertIterableEquals(List.of("/bin/bash", "-c", "cp /a /b;jshell"), shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify runtime options")
  void runtimeOptions(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--runtime-option=--remote");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertIterableEquals(List.of("--remote"), shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify runtime pull options")
  void runtimePullOptions(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--runtime-pull-option=--all-tags");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertIterableEquals(List.of("--all-tags"), shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify runtime build options")
  void runtimeBuildOptions(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--runtime-build-option=--no-cache");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertIterableEquals(List.of("--no-cache"), shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify runtime run options")
  void runtimeRunOptions(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--runtime-run-option=--no-hosts");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertIterableEquals(List.of("--no-hosts"), shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify runtime cleanup options")
  void runtimeCleanupOptions(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--runtime-cleanup-option=--force");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertIterableEquals(List.of("--force"), shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify volumes")
  void volume(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--volume=/abc:/def");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertIterableEquals(List.of("/abc:/def"), shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify environment variables")
  void variables(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--env=key=value");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertIterableEquals(List.of("key=value"), shell.options.variables, "variables"),
      () -> assertNull(shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to publish ports")
  void ports(final String tool) {
    final var shell = parseShellCommand("shell", "--runtime", tool, "--publish=8080:12345");
    assertAll("shell options",
      () -> assertEquals(ShellRuntime.fromAlias(tool), shell.options.runtime, "runtime"),
      () -> assertTrue(shell.options.interactive, "interactive"),
      () -> assertTrue(shell.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(shell.options.debug, "debug"),
      () -> assertFalse(shell.options.removeImage, "removeImage"),
      () -> assertNull(shell.options.dockerfile, "dockerfile"),
      () -> assertNull(shell.options.runtimeOptions, "runtimeOptions"),
      () -> assertNull(shell.options.runtimePullOptions, "runtimePullOptions"),
      () -> assertNull(shell.options.runtimeBuildOptions, "runtimeBuildOptions"),
      () -> assertNull(shell.options.runtimeRunOptions, "runtimeRunOptions"),
      () -> assertNull(shell.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
      () -> assertNull(shell.options.volumes, "volumes"),
      () -> assertNull(shell.options.variables, "variables"),
      () -> assertIterableEquals(List.of("8080:12345"), shell.options.ports, "ports"),
      () -> assertEquals("fedora:latest", shell.options.image, "image"),
      () -> assertNull(shell.options.commands, "commands")
    );
  }

}
