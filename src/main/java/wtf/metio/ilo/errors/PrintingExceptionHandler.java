/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.errors;

import picocli.CommandLine;

public final class PrintingExceptionHandler implements CommandLine.IExecutionExceptionHandler {

  @Override
  public int handleExecutionException(
      final Exception exception,
      final CommandLine commandLine,
      final CommandLine.ParseResult parseResult) {
    // Fall back to the exception's type when it carries no message, so the output is never a bare
    // "null" (BusinessExceptions always have a message; an unexpected exception may not).
    final var message = exception.getMessage();
    commandLine.getErr().println(commandLine.getColorScheme().errorText(
        message != null ? message : exception.toString()));
    final var mapper = commandLine.getExitCodeExceptionMapper();
    return mapper.getExitCode(exception);
  }

}
