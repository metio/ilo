/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo;

import picocli.AutoComplete;
import picocli.CommandLine;
import wtf.metio.ilo.cli.RunCommands;
import wtf.metio.ilo.errors.ExitCodes;
import wtf.metio.ilo.errors.PrintingExceptionHandler;
import wtf.metio.ilo.shell.ShellCommand;
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
        ShellCommand.class,
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
