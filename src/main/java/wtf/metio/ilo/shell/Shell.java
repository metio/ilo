/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import picocli.CommandLine;
import wtf.metio.ilo.exec.Executables;
import wtf.metio.ilo.tools.Tools;

import java.util.concurrent.Callable;
import java.util.stream.IntStream;

@CommandLine.Command(
    name = "shell",
    description = "Opens an (interactive) shell for your build environment",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true,
    showDefaultValues = true,
    descriptionHeading = "%n",
    parameterListHeading = "%n"
)
public class Shell implements Callable<Integer> {

  @CommandLine.Mixin
  public ShellOptions options;

  @Override
  public Integer call() {
    final var tool = Tools.selectShellRuntime(options.runtime);
    final var pullArguments = tool.pullArguments(options);
    final var pullExitCode = Executables.runAndWaitForExit(pullArguments);
    final var buildArguments = tool.buildArguments(options);
    final var buildExitCode = Executables.runAndWaitForExit(buildArguments);
    final var runArguments = tool.runArguments(options);
    final var runExitCode = Executables.runAndWaitForExit(runArguments);
    final var cleanupArguments = tool.cleanupArguments(options);
    final var cleanupExitCode = Executables.runAndWaitForExit(cleanupArguments);
    return IntStream.of(pullExitCode, buildExitCode, runExitCode, cleanupExitCode)
        .max().orElse(CommandLine.ExitCode.SOFTWARE);
  }

}
