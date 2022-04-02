/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("NoMatchingRuntimeException")
class NoMatchingRuntimeExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var exception = new NoMatchingRuntimeException();
    assertAll("exception",
      () -> assertEquals(107, exception.getExitCode(), "exitCode"),
      () -> assertEquals("No matching runtime was found on your system. Select another runtime using '--runtime' or install your preferred runtime on your system.", exception.getMessage(), "message"));
  }

}
