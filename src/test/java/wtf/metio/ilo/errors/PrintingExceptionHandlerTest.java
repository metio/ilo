/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.Ilo;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
