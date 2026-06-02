/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CommandTimedOutException")
class CommandTimedOutExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var exception = new CommandTimedOutException(30, "sh -c sleep 99");
    assertAll("exception",
        () -> assertEquals(124, exception.getExitCode(), "exitCode"),
        () -> assertTrue(exception.getMessage().contains("30 seconds"), "duration"),
        () -> assertTrue(exception.getMessage().contains("sh -c sleep 99"), "command"));
  }

}
