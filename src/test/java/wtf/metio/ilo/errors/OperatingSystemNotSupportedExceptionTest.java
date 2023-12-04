/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("OperatingSystemNotSupportedException")
class OperatingSystemNotSupportedExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var exception = new OperatingSystemNotSupportedException(new UnsupportedOperationException());
    assertAll("exception",
        () -> assertEquals(103, exception.getExitCode(), "exitCode"),
        () -> assertEquals("Your operating system does not support the creation of processes - sadly ilo won't work here.", exception.getMessage(), "message"));
  }

}
