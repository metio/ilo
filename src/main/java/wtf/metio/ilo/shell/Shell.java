/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
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
    usageHelpAutoWidth = true,
    showDefaultValues = true,
    descriptionHeading = "%n",
    parameterListHeading = "%n"
)
public final class Shell implements Callable<Integer> {

  @CommandLine.Mixin
  public ShellOptions options;

  private final CliExecutor<? super ShellRuntime, ? extends ShellCLI> executor;

  // default constructor for picocli
  public Shell() {
    this(new ShellExecutor());
  }

  // constructor for testing
  Shell(final CliExecutor<? super ShellRuntime, ? extends ShellCLI> executor) {
    this.executor = executor;
  }

  @Override
  public Integer call() {
    final var tool = executor.selectRuntime(options.runtime);
    return CommandLifecycle.run(tool, options, executor::execute);
  }

}
