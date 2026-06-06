/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.compose;

import picocli.CommandLine;
import wtf.metio.ilo.cli.ContainerProcesses;
import wtf.metio.ilo.cli.ContainerState;
import wtf.metio.ilo.cli.SessionLifecycle;
import wtf.metio.ilo.errors.RuntimeIOException;
import wtf.metio.ilo.model.CliExecutor;
import wtf.metio.ilo.utils.Strings;
import wtf.metio.ilo.version.VersionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.UnaryOperator;

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
  private final UnaryOperator<String> keepaliveOverrideFile;

  // default constructor for picocli
  public ComposeCommand() {
    this(new ComposeExecutor());
  }

  // constructor for testing
  ComposeCommand(final CliExecutor<? super ComposeRuntime, ComposeCLI, ComposeOptions> executor) {
    this(executor, ComposeOverride::write);
  }

  // constructor for testing with a stubbed keepalive-override writer (so the file path is known)
  ComposeCommand(
      final CliExecutor<? super ComposeRuntime, ComposeCLI, ComposeOptions> executor,
      final UnaryOperator<String> keepaliveOverrideFile) {
    this.executor = executor;
    this.keepaliveOverrideFile = keepaliveOverrideFile;
  }

  @Override
  public Integer call() {
    final var tool = executor.selectRuntime(options.runtime);
    // The keepalive override only applies when there is a service to keep alive; without it ilo cannot
    // identify a single container, so it falls back to stopping the services on exit. The override file
    // is written up front so a write failure degrades to that same unmanaged path instead of aborting.
    var managed = options.overrideCommand && Strings.isNotBlank(options.service);
    String override = null;
    if (managed) {
      try {
        override = keepaliveOverrideFile.apply(options.service);
      } catch (final RuntimeIOException exception) {
        System.err.println("ilo: could not write the keepalive override (" + rootMessage(exception)
            + "), so the services run their own command and reuse detection is unavailable.");
        managed = false;
      }
    }
    if (!managed && Strings.isNotBlank(options.service) && !options.keepRunningOnExit) {
      System.err.println("ilo: without the keepalive override ilo cannot tell whether another terminal "
          + "is attached, so the services are stopped when this session exits; use --keep-running to "
          + "leave them up.");
    }
    final var managedSession = managed;
    final var overrideFile = override;
    final var steps = new SessionLifecycle.Steps(
        tool.removeArguments(options, COMPOSE_PROJECT),
        tool.pullArguments(options),
        tool.buildArguments(options),
        createStep(tool, managedSession, overrideFile),
        tool.startArguments(options, COMPOSE_PROJECT),
        tool.attachArguments(options, COMPOSE_PROJECT),
        // Stop only once this is the last attached session; while another terminal still has the
        // managed service open, the services are left running. Without the override there is nothing
        // to ref-count, so the services are stopped on exit. '--keep-running' leaves them up regardless.
        () -> options.keepRunningOnExit || (managedSession && otherSessionsAttached(tool))
            ? List.of()
            : List.of(tool.stopArguments(options, COMPOSE_PROJECT)));
    // 'up --detach' is idempotent, so a compose session always takes the create path regardless of any
    // observed state.
    return SessionLifecycle.run(steps, SessionLifecycle.Lifecycle.none(),
        options.fresh, options.debug, executor.sessionExecutor(), ContainerState.ABSENT);
  }

  // The cause's message is the most specific, falling back to the exception's own and then its type, so
  // the warning is never a bare "null".
  private static String rootMessage(final Throwable exception) {
    return Optional.ofNullable(exception.getCause()).map(Throwable::getMessage)
        .or(() -> Optional.ofNullable(exception.getMessage()))
        .orElseGet(exception::toString);
  }

  // The keepalive override is layered onto the project's compose file(s) for the 'up' step only; the
  // other steps address the already-running project by name and need no extra file.
  private List<String> createStep(final ComposeCLI tool, final boolean managed, final String overrideFile) {
    if (!managed) {
      return tool.createArguments(options, COMPOSE_PROJECT);
    }
    final var baseFiles = options.file;
    final var withOverride = new ArrayList<>(Optional.ofNullable(baseFiles).orElseGet(List::of));
    withOverride.add(overrideFile);
    options.file = withOverride;
    try {
      return tool.createArguments(options, COMPOSE_PROJECT);
    } finally {
      options.file = baseFiles;
    }
  }

  // With the keepalive as the service's PID 1, any other process in its container is another open
  // session; this session's own exec has already returned by the time the teardown is computed.
  private boolean otherSessionsAttached(final ComposeCLI tool) {
    return ContainerProcesses.hasSessions(executor.capture(tool.processesArguments(options)));
  }

}
