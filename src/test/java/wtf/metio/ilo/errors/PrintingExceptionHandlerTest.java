/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import wtf.metio.ilo.Ilo;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PrintingExceptionHandler")
class PrintingExceptionHandlerTest {

  @Test
  @DisplayName("prints exception message to error output")
  void printsExceptionMessage() {
    final var commandLine = Ilo.commandLine();
    final var writer = new StringWriter();
    final var output = new PrintWriter(writer);
    commandLine.setErr(output);
    final var handler = new PrintingExceptionHandler();

    final var exception = new NoMatchingRuntimeException();
    final var exitCode = handler.handleExecutionException(exception, commandLine, null);

    assertAll("exceptions",
        () -> assertEquals(exception.getExitCode(), exitCode),
        () -> assertEquals("No matching runtime was found on your system. Select another runtime using '--runtime' or install your preferred runtime on your system." + System.lineSeparator(), writer.toString()));
  }

  @Test
  @DisplayName("prints the exception type rather than 'null' when there is no message")
  void printsTypeWhenNoMessage() {
    final var commandLine = Ilo.commandLine();
    final var writer = new StringWriter();
    commandLine.setErr(new PrintWriter(writer));
    final var handler = new PrintingExceptionHandler();

    final var exitCode = handler.handleExecutionException(new IllegalStateException(), commandLine, null);

    assertAll("no message",
        () -> assertEquals(CommandLine.ExitCode.SOFTWARE, exitCode),
        () -> assertTrue(writer.toString().contains("IllegalStateException"), writer.toString()),
        () -> assertFalse(writer.toString().contains("null"), writer.toString()));
  }

}
