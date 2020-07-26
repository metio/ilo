/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import picocli.CommandLine;

import java.util.List;
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

  private final ShellAPI api;

  // default constructor for picocli
  public Shell() {
    this(new ShellExecutor());
  }

  // constructor for testing
  Shell(final ShellAPI api) {
    this.api = api;
  }

  @Override
  public Integer call() {
    final var tool = api.selectRuntime(options.runtime);
    final var pullArguments = tool.pullArguments(options);
    final var pullExitCode = api.execute(pullArguments);
    final var buildArguments = tool.buildArguments(options);
    final var buildExitCode = api.execute(buildArguments);
    final var runArguments = tool.runArguments(options);
    final var runExitCode = api.execute(runArguments);
    final var cleanupArguments = tool.cleanupArguments(options);
    final var cleanupExitCode = api.execute(cleanupArguments);
    return IntStream.of(pullExitCode, buildExitCode, runExitCode, cleanupExitCode)
        .max().orElse(CommandLine.ExitCode.SOFTWARE);
  }

  interface ShellAPI {
    ShellCLI selectRuntime(ShellRuntime runtime);

    int execute(List<String> args);
  }

}
