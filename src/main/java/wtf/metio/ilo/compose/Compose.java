/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import picocli.CommandLine;
import wtf.metio.ilo.cli.AutoSelectRuntime;
import wtf.metio.ilo.cli.Executables;

import java.util.concurrent.Callable;
import java.util.stream.IntStream;

@CommandLine.Command(
    name = "compose",
    description = "Open an (interactive) shell using podman-/docker-compose",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true,
    showDefaultValues = true,
    descriptionHeading = "%n",
    optionListHeading = "%n"
)
public final class Compose implements Callable<Integer> {

  @CommandLine.Mixin
  public ComposeOptions options;

  @Override
  public Integer call() {
    final var tool = AutoSelectRuntime.selectComposeRuntime(options.runtime);
    final var pullArguments = tool.pullArguments(options);
    final var pullExitCode = Executables.runAndWaitForExit(pullArguments, options.debug);
    final var buildArguments = tool.buildArguments(options);
    final var buildExitCode = Executables.runAndWaitForExit(buildArguments, options.debug);
    final var runArguments = tool.runArguments(options);
    final var runExitCode = Executables.runAndWaitForExit(runArguments, options.debug);
    final var cleanupArguments = tool.cleanupArguments(options);
    final var cleanupExitCode = Executables.runAndWaitForExit(cleanupArguments, options.debug);
    return IntStream.of(pullExitCode, buildExitCode, runExitCode, cleanupExitCode)
        .max().orElse(CommandLine.ExitCode.SOFTWARE);
  }

}
