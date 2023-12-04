/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class VersionAcceptanceTest extends CLI_TCK {

  @ParameterizedTest
  @DisplayName("version info")
  @ValueSource(strings = {"-V", "--version"})
  void shouldSupportVersionOption(final String flag) {
    final var exitCode = cmd.execute(flag);
    assertAll("version",
        () -> assertEquals(0, exitCode, "exitCode"),
        () -> assertTrue(output.toString().startsWith("ilo: "), "version"));
  }

}
