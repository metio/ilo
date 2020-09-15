/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class HelpTest extends CLI_TCK {

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

  @DisplayName("compose help")
  @ParameterizedTest
  @ValueSource(strings = {"-h", "--help"})
  void shouldHaveHelpForCompose(final String flag) {
    verifyHelp("compose", flag);
  }

  private void verifyHelp(final String... flags) {
    final var exitCode = cmd.execute(flags);
    assertAll("help",
      () -> assertEquals(0, exitCode, "exitCode"),
      () -> assertTrue(output.toString().startsWith("Usage: ilo"), () -> output.toString()));
  }

}
