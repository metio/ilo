/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("UnexpectedInterruptionException")
class UnexpectedInterruptionExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var exception = new UnexpectedInterruptionException(new InterruptedException());
    assertAll("exception",
        () -> assertEquals(106, exception.getExitCode(), "exitCode"),
        () -> assertEquals("The process was unexpected interrupted. In case you can reproduce this, open a ticket at https://github.com/metio/ilo.", exception.getMessage(), "message"));
  }

}
