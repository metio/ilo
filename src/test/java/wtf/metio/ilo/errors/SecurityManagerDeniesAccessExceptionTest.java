/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SecurityManagerDeniesAccessException")
class SecurityManagerDeniesAccessExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var exception = new SecurityManagerDeniesAccessException(new SecurityException());
    assertAll("exception",
        () -> assertEquals(105, exception.getExitCode(), "exitCode"),
        () -> assertEquals("A Java SecurityManager does not allow creating new processes.", exception.getMessage(), "message"));
  }

}
