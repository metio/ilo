/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.cli;

import picocli.AutoComplete;
import picocli.CommandLine;
import wtf.metio.ilo.cli.commands.OpenShell;
import wtf.metio.ilo.cli.options.ShellOptions;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Main entry point for Ilo - the reproducible build environment manager
 */
@CommandLine.Command(
    name = "ilo",
    description = "Manage reproducible build environments",
    version = "1.0.0",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true,
    subcommands = {
        OpenShell.class,
        AutoComplete.GenerateCompletion.class
    },
    showDefaultValues = true
)
public final class Ilo implements Callable<Integer> {

  @CommandLine.Mixin
  private ShellOptions options;

  public static void main(final String[] args) {
    // TODO: parse .ilo.rc
    System.exit(new CommandLine(new Ilo())
        .execute(Arrays.stream(args)
            .filter(arg -> !Ilo.class.getCanonicalName().equalsIgnoreCase(arg))
            .toArray(String[]::new)));
  }

  @Override
  public Integer call() {
    return new OpenShell(options).call();
  }

}
