/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.commands;

import picocli.CommandLine;
import wtf.metio.ilo.exec.Exec;
import wtf.metio.ilo.exec.Executables;
import wtf.metio.ilo.options.ComposeOptions;
import wtf.metio.ilo.tools.CliTool;
import wtf.metio.ilo.tools.Tools;
import wtf.metio.ilo.utils.CalculateArguments;
import wtf.metio.ilo.utils.Debug;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;

@CommandLine.Command(
    name = "compose",
    description = "Open an (interactive) shell using podman-/docker-compose",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true,
    showDefaultValues = true,
    descriptionHeading = "%n",
    optionListHeading = "%n"
)
public class Compose implements Callable<Integer> {

  @CommandLine.Mixin
  public ComposeOptions options;

  @Override
  public Integer call() {
    final var executables = Exec.executables();
    final var runExitCode = execute(CalculateArguments::composeRunArguments, executables);
    // docker-compose needs an additional cleanup even when using 'run --rm'
    // see https://github.com/docker/compose/issues/2791
    final var cleanupExitCode = execute(CalculateArguments::composeCleanupArguments, executables);
    return Math.max(runExitCode, cleanupExitCode);
  }

  private Integer execute(
      final BiFunction<? super ComposeOptions, ? super String, ? extends List<String>> argumentCreator,
      final Executables executables) {
    return Tools.detectedComposeRuntime(executables)
        .map(CliTool::name)
        .filter(options.runtime::matches)
        .map(tool -> argumentCreator.apply(options, tool))
        .peek(args -> Debug.showExecutedCommand(options.debug, args))
        .map(executables::runAndWaitForExit)
        .findFirst()
        .orElse(CommandLine.ExitCode.USAGE);
  }

}
