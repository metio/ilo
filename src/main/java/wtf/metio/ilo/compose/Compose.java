/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import picocli.CommandLine;
import wtf.metio.ilo.exec.Executables;
import wtf.metio.ilo.tools.Tools;

import java.util.concurrent.Callable;

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
    final var runExitCode = Tools.selectComposeRuntime(options.runtime)
        .map(tool -> tool.arguments(options))
        .map(Executables::runAndWaitForExit)
        .orElse(CommandLine.ExitCode.USAGE);
    // docker-compose needs an additional cleanup even when using 'run --rm'
    // see https://github.com/docker/compose/issues/2791
    final var cleanupExitCode = Tools.selectComposeRuntime(options.runtime)
        .map(tool -> tool.cleanupArguments(options))
        .map(Executables::runAndWaitForExit)
        .orElse(CommandLine.ExitCode.USAGE);
    return Math.max(runExitCode, cleanupExitCode);
  }

}
