/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.errors;

import picocli.CommandLine;

import java.util.Optional;

public final class PrintingExceptionHandler implements CommandLine.IExecutionExceptionHandler {

  @Override
  public int handleExecutionException(
      final Exception exception,
      final CommandLine commandLine,
      final CommandLine.ParseResult parseResult) {
    commandLine.getErr().println(commandLine.getColorScheme().errorText(exception.getMessage()));
    return Optional.of(commandLine)
        .map(CommandLine::getExitCodeExceptionMapper)
        .map(mapper -> mapper.getExitCode(exception))
        .orElseGet(() -> commandLine.getCommandSpec().exitCodeOnExecutionException());
  }

}
