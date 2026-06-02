/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.compose;

import picocli.CommandLine;
import wtf.metio.ilo.cli.CommandLifecycle;
import wtf.metio.ilo.model.CliExecutor;
import wtf.metio.ilo.version.VersionProvider;

import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "compose",
    description = "Open an (interactive) shell using podman-/docker-compose",
    versionProvider = VersionProvider.class,
    mixinStandardHelpOptions = true,
    showAtFileInUsageHelp = true,
    usageHelpAutoWidth = true,
    showDefaultValues = true,
    descriptionHeading = "%n",
    optionListHeading = "%n"
)
public final class ComposeCommand implements Callable<Integer> {

  @CommandLine.Mixin
  public ComposeOptions options;

  private final CliExecutor<? super ComposeRuntime, ComposeCLI, ComposeOptions> executor;

  // default constructor for picocli
  public ComposeCommand() {
    this(new ComposeExecutor());
  }

  // constructor for testing
  ComposeCommand(final CliExecutor<? super ComposeRuntime, ComposeCLI, ComposeOptions> executor) {
    this.executor = executor;
  }

  @Override
  public Integer call() {
    final var tool = executor.selectRuntime(options.runtime);
    return CommandLifecycle.run(tool, options, executor::execute);
  }

}
