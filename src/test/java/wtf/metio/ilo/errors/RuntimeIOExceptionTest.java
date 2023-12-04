/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("RuntimeIOException")
class RuntimeIOExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var exception = new RuntimeIOException(new IOException());
    assertAll("exception",
        () -> assertEquals(104, exception.getExitCode(), "exitCode"),
        () -> assertEquals("I/O error occurred.", exception.getMessage(), "message"));
  }

}
