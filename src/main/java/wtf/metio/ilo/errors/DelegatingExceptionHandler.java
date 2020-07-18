/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.errors;

import picocli.CommandLine;
import wtf.metio.ilo.commands.Compose;
import wtf.metio.ilo.commands.Shell;
import wtf.metio.ilo.exec.Exec;
import wtf.metio.ilo.tools.Tools;

import java.io.IOException;

public class DelegatingExceptionHandler implements CommandLine.IExecutionExceptionHandler {

  private static Throwable findRootCause(final Throwable throwable) {
    var rootCause = throwable;
    while (null != rootCause.getCause() && rootCause.getCause() != rootCause) {
      rootCause = rootCause.getCause();
    }
    return rootCause;
  }

  @Override
  public int handleExecutionException(
      final Exception exception,
      final CommandLine commandLine,
      final CommandLine.ParseResult parseResult) {
    final var rootCause = findRootCause(exception);
    if (rootCause instanceof IOException) {
      final var subcommand = parseResult.subcommand();
      final var commandName = subcommand.commandSpec().name();
      final var program = "shell".equals(commandName)
          ? ((Shell) subcommand.commandSpec().userObject()).options.runtime
          : ((Compose) subcommand.commandSpec().userObject()).options.runtime;
      commandLine.getErr().println("Could not find program: '" + program + "'");
      commandLine.getErr().println();
      commandLine.getErr().println("There are two ways to solve this problem:");
      commandLine.getErr().println("  1) Install the specified runtime");
      commandLine.getErr().println("  2) Specify a different runtime using --runtime ...");
      commandLine.getErr().println("  3) Specify no value for --runtime and let ilo pick the right value for you");
      final var executables = Exec.executables();
      final var runtimes = "shell".equals(commandName)
          ? Tools.detectedShellRuntime(executables, null)
          : Tools.detectedComposeRuntime(executables, null);
      commandLine.getErr().println();
      commandLine.getErr().println("Available runtimes on your system:");
      runtimes.forEach(runtime -> commandLine.getErr().println("  - " + runtime));
    } else {
      commandLine.getErr().println(exception.getMessage());
      exception.printStackTrace();
    }

    return null != commandLine.getExitCodeExceptionMapper()
        ? commandLine.getExitCodeExceptionMapper().getExitCode(exception)
        : commandLine.getCommandSpec().exitCodeOnExecutionException();
  }

}
