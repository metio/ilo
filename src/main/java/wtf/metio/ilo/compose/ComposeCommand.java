/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.compose;

import picocli.CommandLine;
import wtf.metio.ilo.cli.ContainerState;
import wtf.metio.ilo.cli.SessionLifecycle;
import wtf.metio.ilo.model.CliExecutor;
import wtf.metio.ilo.version.VersionProvider;

import java.util.List;
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

  // 'up --detach' is idempotent, so a session always takes the create path; the state probe is not
  // consulted (compose manages per-service state itself rather than exposing a single container).
  private static final String COMPOSE_PROJECT = "";

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
    final var steps = new SessionLifecycle.Steps(
        List.of(),
        tool.removeArguments(options, COMPOSE_PROJECT),
        tool.pullArguments(options),
        tool.buildArguments(options),
        tool.createArguments(options, COMPOSE_PROJECT),
        tool.startArguments(options, COMPOSE_PROJECT),
        tool.attachArguments(options, COMPOSE_PROJECT),
        // '--keep-running' leaves the services up after exit instead of stopping them.
        options.keepRunning ? List.of() : List.of(tool.stopArguments(options, COMPOSE_PROJECT)));
    return SessionLifecycle.run(steps, SessionLifecycle.Lifecycle.none(),
        options.fresh, options.debug, executor::execute, arguments -> ContainerState.ABSENT);
  }

}
