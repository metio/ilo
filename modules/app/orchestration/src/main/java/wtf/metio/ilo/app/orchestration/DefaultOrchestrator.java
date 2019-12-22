/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.app.orchestration;

import wtf.metio.ilo.app.help.PrintHelp;
import wtf.metio.ilo.app.user_input.ArgumentsParser;
import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.tools.api.CliTool;

import java.util.ArrayList;
import java.util.List;

final class DefaultOrchestrator implements Orchestrator {

  private final Executables executables;

  DefaultOrchestrator(final Executables executables) {
    this.executables = executables;
  }

  @Override
  public List<Command> determineCommands(final String[] arguments, final List<? extends CliTool> tools) {
    final var args = ArgumentsParser.parse(arguments);
    final var commands = new ArrayList<Command>();
    if (args.showHelp) {
      commands.add(new RunnableCommand(() -> PrintHelp.help(tools.stream(), 0, args.debug), 1));
    } else {
      final var currentDir = System.getProperty("user.dir");
      tools.stream()
          .filter(tool -> args.runtime.equalsIgnoreCase(tool.name()))
          .findFirst()
          .map(runtime -> new CliCommand(runtime, args.debug, "run",
              "--interactive",
              "--tty",
              "--rm",
              "--hostname", "build-env",
              "--label", "wtf.metio.ilo.created=" + System.currentTimeMillis(),
              "--volume", currentDir + ":" + currentDir + ":Z",
              "--workdir", currentDir,
              args.image,
              args.command))
          .ifPresent(commands::add);
    }
    if (commands.isEmpty()) {
      commands.add(new RunnableCommand(() -> PrintHelp.help(tools.stream(), 1, args.debug), 1));
    }
    return commands;
  }

  @Override
  public int runCommand(final Command command) {
    return command.run(executables);
  }

}
