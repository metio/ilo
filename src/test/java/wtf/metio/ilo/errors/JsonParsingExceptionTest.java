/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("JsonParsingException")
class JsonParsingExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var exception = new JsonParsingException(new RuntimeException());
    assertAll("exception",
        () -> assertEquals(109, exception.getExitCode(), "exitCode"),
        () -> assertEquals("The devcontainer JSON file could not be parsed. Make sure it contains valid JSON.", exception.getMessage(), "message"));
  }

}
