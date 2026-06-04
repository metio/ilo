/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import wtf.metio.ilo.shell.ShellRuntime;
import wtf.metio.ilo.test.TestMethodSources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Runtime")
@ExtendWith(SystemStubsExtension.class)
class RuntimeTest extends TestMethodSources {

  private static final String VARIABLE = "ILO_RUNTIME_TEST";

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("finds first matching shell runtime")
  void findMatchingShellRuntime(final String runtime) {
    assertNotNull(Runtime.firstMatching(runtime, ShellRuntime.values()));
  }

  @Test
  @DisplayName("reads no preference from an unset environment variable")
  void noPreferenceWhenUnset() {
    assertTrue(Runtime.fromEnvironment(VARIABLE, ShellRuntime.values()).isEmpty());
  }

  @Test
  @DisplayName("reads no preference from a blank environment variable")
  void noPreferenceWhenBlank(final EnvironmentVariables environmentVariables) {
    environmentVariables.set(VARIABLE, "   ");
    assertTrue(Runtime.fromEnvironment(VARIABLE, ShellRuntime.values()).isEmpty());
  }

  @Test
  @DisplayName("resolves a recognized environment variable to its runtime")
  void resolvesRecognizedValue(final EnvironmentVariables environmentVariables) {
    environmentVariables.set(VARIABLE, "podman");
    assertEquals(ShellRuntime.PODMAN, Runtime.fromEnvironment(VARIABLE, ShellRuntime.values()).orElseThrow());
  }

  @Test
  @DisplayName("ignores an unrecognized value and reports it instead of aborting")
  void reportsUnrecognizedValue(final EnvironmentVariables environmentVariables, final SystemErr systemErr) {
    environmentVariables.set(VARIABLE, "bogus");
    assertTrue(Runtime.fromEnvironment(VARIABLE, ShellRuntime.values()).isEmpty());
    assertTrue(systemErr.getText().contains("bogus"), systemErr.getText());
    assertTrue(systemErr.getText().contains(VARIABLE), systemErr.getText());
  }

}
