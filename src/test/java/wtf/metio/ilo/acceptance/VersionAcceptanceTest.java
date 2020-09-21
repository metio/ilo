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
