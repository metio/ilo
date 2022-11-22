/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
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
public final class Compose implements Callable<Integer> {

  @CommandLine.Mixin
  public ComposeOptions options;

  private final CliExecutor<? super ComposeRuntime, ComposeCLI, ComposeOptions> executor;

  // default constructor for picocli
  public Compose() {
    this(new ComposeExecutor());
  }

  // constructor for testing
  Compose(final CliExecutor<? super ComposeRuntime, ComposeCLI, ComposeOptions> executor) {
    this.executor = executor;
  }

  @Override
  public Integer call() {
    final var tool = executor.selectRuntime(options.runtime);
    return CommandLifecycle.run(tool, options, executor::execute);
  }

}
