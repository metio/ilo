/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("DevfileYamlMissingException")
class DevfileYamlMissingExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var exception = new DevfileYamlMissingException();
    assertAll("exception",
        () -> assertEquals(111, exception.getExitCode(), "exitCode"),
        () -> assertEquals("No devfile YAML file found. Create one either at 'devfile.yaml' or '.devfile.yaml'.", exception.getMessage(), "message"));
  }

}
