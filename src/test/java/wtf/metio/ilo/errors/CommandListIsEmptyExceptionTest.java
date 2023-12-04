/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CommandListIsEmptyException")
class CommandListIsEmptyExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var exception = new CommandListIsEmptyException(new IndexOutOfBoundsException());
    assertAll("exception",
        () -> assertEquals(102, exception.getExitCode(), "exitCode"),
        () -> assertEquals("The generated command list is empty - this is a bug in ilo!", exception.getMessage(), "message"));
  }

}
