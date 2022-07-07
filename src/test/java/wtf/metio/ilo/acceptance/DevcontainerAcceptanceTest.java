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
import wtf.metio.ilo.compose.ComposeRuntime;
import wtf.metio.ilo.shell.ShellRuntime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ilo devcontainer")
class DevcontainerAcceptanceTest extends CLI_TCK {

  private static final List<String> DEFAULT_LOCATIONS = List.of(".devcontainer/devcontainer.json", ".devcontainer.json");

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
    final var devcontainer = parseDevcontainerCommand("devcontainer");
    assertAll("devcontainer options",
      () -> assertTrue(devcontainer.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(devcontainer.options.debug, "debug"),
      () -> assertFalse(devcontainer.options.removeImage, "removeImage"),
      () -> assertFalse(devcontainer.options.pull, "pull"),
      () -> assertIterableEquals(DEFAULT_LOCATIONS, devcontainer.options.locations, "locations")
    );
  }

  @Test
  @DisplayName("custom devcontainer.json location")
  void customLocation() {
    final var devcontainer = parseDevcontainerCommand("devcontainer", "somewhere.json");
    assertAll("devcontainer options",
      () -> assertTrue(devcontainer.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(devcontainer.options.debug, "debug"),
      () -> assertFalse(devcontainer.options.removeImage, "removeImage"),
      () -> assertFalse(devcontainer.options.pull, "pull"),
      () -> assertIterableEquals(List.of("somewhere.json"), devcontainer.options.locations, "locations")
    );
  }

  @Test
  @DisplayName("custom devcontainer.json locations")
  void customLocations() {
    final var devcontainer = parseDevcontainerCommand("devcontainer", "somewhere.json", "other.json");
    assertAll("devcontainer options",
      () -> assertTrue(devcontainer.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(devcontainer.options.debug, "debug"),
      () -> assertFalse(devcontainer.options.removeImage, "removeImage"),
      () -> assertFalse(devcontainer.options.pull, "pull"),
      () -> assertIterableEquals(List.of("somewhere.json", "other.json"), devcontainer.options.locations, "locations")
    );
  }

  @Test
  @DisplayName("allows to disable mounting the project directory")
  void disableMountProjectDir() {
    final var devcontainer = parseDevcontainerCommand("devcontainer", "--mount-project-dir=false");
    assertAll("devcontainer options",
      () -> assertFalse(devcontainer.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(devcontainer.options.debug, "debug"),
      () -> assertFalse(devcontainer.options.removeImage, "removeImage"),
      () -> assertFalse(devcontainer.options.pull, "pull"),
      () -> assertIterableEquals(DEFAULT_LOCATIONS, devcontainer.options.locations, "locations")
    );
  }

  @Test
  @DisplayName("allows to enable debugging")
  void enableDebug() {
    final var devcontainer = parseDevcontainerCommand("devcontainer", "--debug=true");
    assertAll("devcontainer options",
      () -> assertTrue(devcontainer.options.mountProjectDir, "mountProjectDir"),
      () -> assertTrue(devcontainer.options.debug, "debug"),
      () -> assertFalse(devcontainer.options.removeImage, "removeImage"),
      () -> assertFalse(devcontainer.options.pull, "pull"),
      () -> assertIterableEquals(DEFAULT_LOCATIONS, devcontainer.options.locations, "locations")
    );
  }

  @Test
  @DisplayName("allows to remove images after exit")
  void removeImage() {
    final var devcontainer = parseDevcontainerCommand("devcontainer", "--remove-image=true");
    assertAll("devcontainer options",
      () -> assertTrue(devcontainer.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(devcontainer.options.debug, "debug"),
      () -> assertTrue(devcontainer.options.removeImage, "removeImage"),
      () -> assertFalse(devcontainer.options.pull, "pull"),
      () -> assertIterableEquals(DEFAULT_LOCATIONS, devcontainer.options.locations, "locations")
    );
  }

  @Test
  @DisplayName("allows to pull images before run")
  void pullImage() {
    final var devcontainer = parseDevcontainerCommand("devcontainer", "--pull=true");
    assertAll("devcontainer options",
      () -> assertTrue(devcontainer.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(devcontainer.options.debug, "debug"),
      () -> assertFalse(devcontainer.options.removeImage, "removeImage"),
      () -> assertTrue(devcontainer.options.pull, "pull"),
      () -> assertIterableEquals(DEFAULT_LOCATIONS, devcontainer.options.locations, "locations")
    );
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("supports multiple compose runtimes")
  void selectComposeRuntime(final String composeRuntime) {
    final var devcontainer = parseDevcontainerCommand("devcontainer", "--compose-runtime=" + composeRuntime);
    assertAll("devcontainer options",
      () -> assertEquals(ComposeRuntime.fromAlias(composeRuntime), devcontainer.options.composeRuntime, "composeRuntime"),
      () -> assertTrue(devcontainer.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(devcontainer.options.debug, "debug"),
      () -> assertFalse(devcontainer.options.removeImage, "removeImage"),
      () -> assertFalse(devcontainer.options.pull, "pull"),
      () -> assertIterableEquals(DEFAULT_LOCATIONS, devcontainer.options.locations, "locations")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("supports multiple shell runtimes")
  void selectShellRuntime(final String shellRuntime) {
    final var devcontainer = parseDevcontainerCommand("devcontainer", "--shell-runtime=" + shellRuntime);
    assertAll("devcontainer options",
      () -> assertEquals(ShellRuntime.fromAlias(shellRuntime), devcontainer.options.shellRuntime, "shellRuntime"),
      () -> assertTrue(devcontainer.options.mountProjectDir, "mountProjectDir"),
      () -> assertFalse(devcontainer.options.debug, "debug"),
      () -> assertFalse(devcontainer.options.removeImage, "removeImage"),
      () -> assertFalse(devcontainer.options.pull, "pull"),
      () -> assertIterableEquals(DEFAULT_LOCATIONS, devcontainer.options.locations, "locations")
    );
  }

}
