/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import picocli.CommandLine;
import wtf.metio.ilo.cli.CommandLifecycle;
import wtf.metio.ilo.model.CliExecutor;
import wtf.metio.ilo.version.VersionProvider;

import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "shell",
    description = "Opens an (interactive) shell for your build environment",
    versionProvider = VersionProvider.class,
    mixinStandardHelpOptions = true,
    showAtFileInUsageHelp = true,
    usageHelpAutoWidth = true,
    showDefaultValues = true,
    descriptionHeading = "%n",
    parameterListHeading = "%n"
)
public final class ShellCommand implements Callable<Integer> {

  @CommandLine.Mixin
  public ShellOptions options;

  private final CliExecutor<? super ShellRuntime, ShellCLI, ShellOptions> executor;

  // default constructor for picocli
  public ShellCommand() {
    this(new ShellExecutor());
  }

  // constructor for testing
  ShellCommand(final CliExecutor<? super ShellRuntime, ShellCLI, ShellOptions> executor) {
    this.executor = executor;
  }

  @Override
  public Integer call() {
    final var tool = executor.selectRuntime(options.runtime);
    return CommandLifecycle.run(tool, options, executor::execute);
  }

}
