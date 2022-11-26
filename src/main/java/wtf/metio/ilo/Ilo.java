/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo;

import picocli.AutoComplete;
import picocli.CommandLine;
import wtf.metio.ilo.cli.RunCommands;
import wtf.metio.ilo.compose.Compose;
import wtf.metio.ilo.devcontainer.Devcontainer;
import wtf.metio.ilo.devfile.Devfile;
import wtf.metio.ilo.errors.ExitCodes;
import wtf.metio.ilo.errors.PrintingExceptionHandler;
import wtf.metio.ilo.shell.Shell;
import wtf.metio.ilo.version.VersionProvider;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Main entry point for ilo - a little tool to manage reproducible build environments
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
    Devcontainer.class,
    Devfile.class,
    AutoComplete.GenerateCompletion.class
  },
  showDefaultValues = true
)
public final class Ilo implements Runnable {

  @CommandLine.Spec
  CommandLine.Model.CommandSpec spec;

  public static void main(final String... userInput) {
    System.setProperty("picocli.disable.closures", "true");
    System.exit(commandLine().execute(allArguments(userInput)));
  }

  // visible for testing
  static String[] allArguments(final String[] userInput) {
    return Stream.concat(runCommands(userInput), Arrays.stream(userInput)).toArray(String[]::new);
  }

  // visible for testing
  static Stream<String> runCommands(final String[] userInput) {
    if (RunCommands.shouldAddRunCommands(userInput)) {
      final var currentDir = Paths.get(System.getProperty("user.dir"));
      return RunCommands.locate(currentDir);
    }
    return Stream.empty();
  }

  // visible for testing
  public static CommandLine commandLine() {
    final var commandLine = new CommandLine(new Ilo());
    commandLine.setStopAtPositional(true);
    commandLine.setCaseInsensitiveEnumValuesAllowed(true);
    commandLine.setExecutionExceptionHandler(new PrintingExceptionHandler());
    commandLine.setExitCodeExceptionMapper(new ExitCodes());
    return commandLine;
  }

  @Override
  public void run() {
    throw new CommandLine.ParameterException(spec.commandLine(), "ERROR: Missing required subcommand" + System.lineSeparator());
  }

}
