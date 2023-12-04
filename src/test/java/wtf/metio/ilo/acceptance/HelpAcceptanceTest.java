/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class HelpAcceptanceTest extends CLI_TCK {

  @DisplayName("usage help")
  @ParameterizedTest
  @ValueSource(strings = {"-h", "--help"})
  void shouldHaveUsageHelp(final String flag) {
    verifyHelp(flag);
  }

  @DisplayName("shell help")
  @ParameterizedTest
  @ValueSource(strings = {"-h", "--help"})
  void shouldHaveHelpForShell(final String flag) {
    verifyHelp("shell", flag);
  }

  private void verifyHelp(final String... flags) {
    final var exitCode = cmd.execute(flags);
    assertAll("help",
        () -> assertEquals(0, exitCode, "exitCode"),
        () -> assertTrue(output.toString().startsWith("Usage: ilo"), () -> output.toString()));
  }

}
