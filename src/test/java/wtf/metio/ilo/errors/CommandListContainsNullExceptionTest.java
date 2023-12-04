/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CommandListContainsNullException")
class CommandListContainsNullExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var values = new ArrayList<String>();
    values.add("test");
    values.add(null);
    final var exception = new CommandListContainsNullException(new NullPointerException(), values);
    assertAll("exception",
        () -> assertEquals(101, exception.getExitCode(), "exitCode"),
        () -> assertEquals("[test, null]", exception.getMessage(), "message"));
  }

}
