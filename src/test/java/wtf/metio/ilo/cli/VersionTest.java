/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class VersionTest extends CLI_TCK {

  @ParameterizedTest
  @DisplayName("version info")
  @ValueSource(strings = {"-V", "--version"})
  void shouldSupportVersionOption(final String flag) {
    final var exitCode = cmd.execute(flag);
    Assertions.assertEquals(0, exitCode);
    Assertions.assertTrue(output.toString().startsWith("ilo: "));
  }

}
