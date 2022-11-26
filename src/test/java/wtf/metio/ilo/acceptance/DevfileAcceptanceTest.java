/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.acceptance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import wtf.metio.ilo.shell.ShellRuntime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ilo devfile")
class DevfileAcceptanceTest extends CLI_TCK {

  private static final List<String> DEFAULT_LOCATIONS = List.of("devfile.yaml", ".devfile.yaml");

  @ParameterizedTest
  @DisplayName("version info")
  @ValueSource(strings = {"-V", "--version"})
  void shouldSupportVersionOption(final String flag) {
    final var exitCode = cmd.execute(flag);
    Assertions.assertEquals(0, exitCode);
    Assertions.assertTrue(output.toString().startsWith("ilo: "));
  }

  @Test
  @DisplayName("default command line options")
  void defaultCommandLine() {
    final var devfile = parseDevfileCommand("devfile");
    assertAll("devfile options",
        () -> assertFalse(devfile.options.debug, "debug"),
        () -> assertFalse(devfile.options.removeImage, "removeImage"),
        () -> assertFalse(devfile.options.pull, "pull"),
        () -> assertNull(devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertIterableEquals(DEFAULT_LOCATIONS, devfile.options.locations, "locations")
    );
  }

  @Test
  @DisplayName("custom devfile.yaml location")
  void customLocation() {
    final var devfile = parseDevfileCommand("devfile", "somewhere.yaml");
    assertAll("devfile options",
        () -> assertFalse(devfile.options.debug, "debug"),
        () -> assertFalse(devfile.options.removeImage, "removeImage"),
        () -> assertFalse(devfile.options.pull, "pull"),
        () -> assertNull(devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertIterableEquals(List.of("somewhere.yaml"), devfile.options.locations, "locations")
    );
  }

  @Test
  @DisplayName("custom devfile.yaml locations")
  void customLocations() {
    final var devfile = parseDevfileCommand("devfile", "somewhere.yaml", "other.yaml");
    assertAll("devfile options",
        () -> assertFalse(devfile.options.debug, "debug"),
        () -> assertFalse(devfile.options.removeImage, "removeImage"),
        () -> assertFalse(devfile.options.pull, "pull"),
        () -> assertNull(devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertIterableEquals(List.of("somewhere.yaml", "other.yaml"), devfile.options.locations, "locations")
    );
  }

  @Test
  @DisplayName("allows to enable debugging")
  void enableDebug() {
    final var devfile = parseDevfileCommand("devfile", "--debug=true");
    assertAll("devfile options",
        () -> assertTrue(devfile.options.debug, "debug"),
        () -> assertFalse(devfile.options.removeImage, "removeImage"),
        () -> assertFalse(devfile.options.pull, "pull"),
        () -> assertNull(devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertIterableEquals(DEFAULT_LOCATIONS, devfile.options.locations, "locations")
    );
  }

  @Test
  @DisplayName("allows to remove images after exit")
  void removeImage() {
    final var devfile = parseDevfileCommand("devfile", "--remove-image=true");
    assertAll("devfile options",
        () -> assertFalse(devfile.options.debug, "debug"),
        () -> assertTrue(devfile.options.removeImage, "removeImage"),
        () -> assertFalse(devfile.options.pull, "pull"),
        () -> assertNull(devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertIterableEquals(DEFAULT_LOCATIONS, devfile.options.locations, "locations")
    );
  }

  @Test
  @DisplayName("allows to pull images before run")
  void pullImage() {
    final var devfile = parseDevfileCommand("devfile", "--pull=true");
    assertAll("devfile options",
        () -> assertFalse(devfile.options.debug, "debug"),
        () -> assertFalse(devfile.options.removeImage, "removeImage"),
        () -> assertTrue(devfile.options.pull, "pull"),
        () -> assertNull(devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertIterableEquals(DEFAULT_LOCATIONS, devfile.options.locations, "locations")
    );
  }

  @Test
  @DisplayName("allows to specify component")
  void component() {
    final var devfile = parseDevfileCommand("devfile", "--component=some-name");
    assertAll("devfile options",
        () -> assertFalse(devfile.options.debug, "debug"),
        () -> assertFalse(devfile.options.removeImage, "removeImage"),
        () -> assertFalse(devfile.options.pull, "pull"),
        () -> assertNull(devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertEquals("some-name", devfile.options.component, "component"),
        () -> assertIterableEquals(DEFAULT_LOCATIONS, devfile.options.locations, "locations")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify runtime options")
  void runtimeOptions(final String tool) {
    final var devfile = parseDevfileCommand("devfile", "--runtime", tool, "--runtime-option=--remote");
    assertAll("devfile options",
        () -> assertEquals(ShellRuntime.fromAlias(tool), devfile.options.runtime, "runtime"),
        () -> assertFalse(devfile.options.debug, "debug"),
        () -> assertFalse(devfile.options.removeImage, "removeImage"),
        () -> assertIterableEquals(List.of("--remote"), devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertIterableEquals(DEFAULT_LOCATIONS, devfile.options.locations, "locations")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify runtime pull options")
  void runtimePullOptions(final String tool) {
    final var devfile = parseDevfileCommand("devfile", "--runtime", tool, "--runtime-pull-option=--all-tags");
    assertAll("devfile options",
        () -> assertEquals(ShellRuntime.fromAlias(tool), devfile.options.runtime, "runtime"),
        () -> assertFalse(devfile.options.debug, "debug"),
        () -> assertFalse(devfile.options.removeImage, "removeImage"),
        () -> assertNull(devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertIterableEquals(List.of("--all-tags"), devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertIterableEquals(DEFAULT_LOCATIONS, devfile.options.locations, "locations")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify runtime build options")
  void runtimeBuildOptions(final String tool) {
    final var devfile = parseDevfileCommand("devfile", "--runtime", tool, "--runtime-build-option=--no-cache");
    assertAll("devfile options",
        () -> assertEquals(ShellRuntime.fromAlias(tool), devfile.options.runtime, "runtime"),
        () -> assertFalse(devfile.options.debug, "debug"),
        () -> assertFalse(devfile.options.removeImage, "removeImage"),
        () -> assertNull(devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertIterableEquals(List.of("--no-cache"), devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertIterableEquals(DEFAULT_LOCATIONS, devfile.options.locations, "locations")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify runtime run options")
  void runtimeRunOptions(final String tool) {
    final var devfile = parseDevfileCommand("devfile", "--runtime", tool, "--runtime-run-option=--no-hosts");
    assertAll("devfile options",
        () -> assertEquals(ShellRuntime.fromAlias(tool), devfile.options.runtime, "runtime"),
        () -> assertFalse(devfile.options.debug, "debug"),
        () -> assertFalse(devfile.options.removeImage, "removeImage"),
        () -> assertNull(devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertIterableEquals(List.of("--no-hosts"), devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertIterableEquals(DEFAULT_LOCATIONS, devfile.options.locations, "locations")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("allows to specify runtime cleanup options")
  void runtimeCleanupOptions(final String tool) {
    final var devfile = parseDevfileCommand("devfile", "--runtime", tool, "--runtime-cleanup-option=--force");
    assertAll("devfile options",
        () -> assertEquals(ShellRuntime.fromAlias(tool), devfile.options.runtime, "runtime"),
        () -> assertFalse(devfile.options.debug, "debug"),
        () -> assertFalse(devfile.options.removeImage, "removeImage"),
        () -> assertNull(devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertIterableEquals(List.of("--force"), devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertIterableEquals(DEFAULT_LOCATIONS, devfile.options.locations, "locations")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("supports multiple devfile runtimes")
  void selectRuntime(final String runtime) {
    final var devfile = parseDevfileCommand("devfile", "--runtime=" + runtime);
    assertAll("devfile options",
        () -> assertEquals(ShellRuntime.fromAlias(runtime), devfile.options.runtime, "runtime"),
        () -> assertFalse(devfile.options.debug, "debug"),
        () -> assertFalse(devfile.options.removeImage, "removeImage"),
        () -> assertFalse(devfile.options.pull, "pull"),
        () -> assertNull(devfile.options.runtimeOptions, "runtimeOptions"),
        () -> assertNull(devfile.options.runtimePullOptions, "runtimePullOptions"),
        () -> assertNull(devfile.options.runtimeBuildOptions, "runtimeBuildOptions"),
        () -> assertNull(devfile.options.runtimeRunOptions, "runtimeRunOptions"),
        () -> assertNull(devfile.options.runtimeCleanupOptions, "runtimeCleanupOptions"),
        () -> assertIterableEquals(DEFAULT_LOCATIONS, devfile.options.locations, "locations")
    );
  }

}
