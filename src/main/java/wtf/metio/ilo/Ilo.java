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
import wtf.metio.ilo.config.Formats;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Main entry point for Ilo - a little tool to manage reproducible build environments
 */
@CommandLine.Command(
    name = "ilo",
    description = "Manage reproducible build environments",
    version = "2.0.0",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true,
    customSynopsis = {
        "ilo [OPTIONS] [COMMANDS...]",
    },
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
public final class Ilo {

  private static final List<String> COMMANDS = List.of("shell", "compose");

  public static void main(final String[] args) {
    final var command = determineCommand(args);
    final var config = Formats.runtimeConfig();
    final var rcValues = config.getOrDefault(command, List.of());

    final var arguments = Stream.concat(Arrays.stream(args), rcValues.stream())
        .filter(arg -> !Ilo.class.getCanonicalName().equalsIgnoreCase(arg)) // workaround for IntelliJ
        .toArray(String[]::new);

    final var commandLine = new CommandLine(new Ilo());
    commandLine.setUnmatchedArgumentsAllowed(true); // workaround for IntelliJ
    System.exit(commandLine.execute(arguments));
  }

  private static String determineCommand(final String[] args) {
    return Arrays.stream(args).filter(COMMANDS::contains).findFirst().orElse("UNKNOWN");
  }

}
