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
    commandLine.getErr().println(commandLine.getColorScheme().errorText(exception.getMessage()));
    final var mapper = commandLine.getExitCodeExceptionMapper();
    return mapper.getExitCode(exception);
  }

}
