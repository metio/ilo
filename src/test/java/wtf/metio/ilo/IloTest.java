/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;
import wtf.metio.ilo.cli.RcTrust;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static wtf.metio.ilo.test.TestResources.testResources;

@DisplayName("Ilo")
@ExtendWith(SystemStubsExtension.class)
class IloTest {

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("reads trusted .rc files")
  void supportsRunCommands(
      @TempDir final Path trustStore,
      final SystemProperties properties,
      final EnvironmentVariables environment) throws IOException {
    final var directory = testResources(Ilo.class).resolve("root").toAbsolutePath();
    trust(environment, trustStore, directory.resolve(".ilo.rc"));
    properties.set("user.dir", directory.toString());
    assertEquals(1, Ilo.runCommands(new String[]{}).count());
  }

  @Test
  @DisplayName("does not need .rc files")
  void runsWithRunCommands(final SystemProperties properties) {
    properties.set("user.dir", testResources(Ilo.class).resolve("empty").toAbsolutePath().toString());
    assertEquals(0, Ilo.runCommands(new String[]{}).count());
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("prepends located run command files in front of the user input")
  void prependsRunCommands(
      @TempDir final Path trustStore,
      final SystemProperties properties,
      final EnvironmentVariables environment) throws IOException {
    final var directory = testResources(Ilo.class).resolve("root").toAbsolutePath();
    trust(environment, trustStore, directory.resolve(".ilo.rc"));
    properties.set("user.dir", directory.toString());
    final var arguments = Ilo.allArguments(new String[]{"shell"});
    assertEquals(2, arguments.length);
    assertEquals("shell", arguments[1]);
  }

  @Test
  @DisplayName("keeps the user input as-is when no run command files are found")
  void keepsUserInputWithoutRunCommands(final SystemProperties properties) {
    properties.set("user.dir", testResources(Ilo.class).resolve("empty").toAbsolutePath().toString());
    assertArrayEquals(new String[]{"shell", "--debug"}, Ilo.allArguments(new String[]{"shell", "--debug"}));
  }

  private static void trust(
      final EnvironmentVariables environment,
      final Path trustStore,
      final Path runCommandFile) throws IOException {
    final var store = Files.createTempFile(trustStore, "trusted", "rc");
    environment.set(wtf.metio.ilo.cli.EnvironmentVariables.ILO_TRUST_FILE.name(), store.toString());
    RcTrust.trust(runCommandFile);
  }

}
