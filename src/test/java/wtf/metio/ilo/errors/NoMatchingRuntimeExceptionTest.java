/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
