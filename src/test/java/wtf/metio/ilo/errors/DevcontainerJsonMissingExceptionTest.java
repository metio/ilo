/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("DevcontainerJsonMissingException")
class DevcontainerJsonMissingExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var exception = new DevcontainerJsonMissingException();
    assertAll("exception",
        () -> assertEquals(108, exception.getExitCode(), "exitCode"),
        () -> assertEquals("No devcontainer JSON file found. Create one either at '.devcontainer/devcontainer.json' or '.devcontainer.json'.", exception.getMessage(), "message"));
  }

}
