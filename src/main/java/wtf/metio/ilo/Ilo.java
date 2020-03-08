/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo;

import picocli.AutoComplete;
import picocli.CommandLine;
import wtf.metio.ilo.commands.Compose;
import wtf.metio.ilo.commands.Shell;
import wtf.metio.ilo.errors.ExecutionExceptionHandler;
import wtf.metio.ilo.version.VersionProvider;

import java.util.Arrays;

/**
 * Main entry point for Ilo - a little tool to manage reproducible build environments
 */
@CommandLine.Command(
    name = "ilo",
    description = "Manage reproducible build environments",
    versionProvider = VersionProvider.class,
    mixinStandardHelpOptions = true,
    showAtFileInUsageHelp = true,
    usageHelpAutoWidth = true,
    synopsisSubcommandLabel = "COMMAND",
    descriptionHeading = "%n",
    parameterListHeading = "%n",
    optionListHeading = "%nOptions:%n",
    commandListHeading = "%nCommands:%n",
    subcommands = {
        Shell.class,
        Compose.class,
        AutoComplete.GenerateCompletion.class
    },
    showDefaultValues = true
)
public final class Ilo implements Runnable {

  @CommandLine.Spec
  CommandLine.Model.CommandSpec spec;

  public static void main(final String[] args) {
    final var arguments = Arrays.stream(args)
        .filter(arg -> !Ilo.class.getCanonicalName().equalsIgnoreCase(arg)) // workaround for IntelliJ
        .toArray(String[]::new);

    final var commandLine = new CommandLine(new Ilo());
    commandLine.setUnmatchedArgumentsAllowed(true); // workaround for IntelliJ
    commandLine.setExecutionExceptionHandler(new ExecutionExceptionHandler());
    System.exit(commandLine.execute(arguments));
  }

  @Override
  public void run() {
    throw new CommandLine.ParameterException(spec.commandLine(), "ERROR: Missing required subcommand" + System.lineSeparator());
  }

}
