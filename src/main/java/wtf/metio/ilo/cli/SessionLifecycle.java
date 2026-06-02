/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.cli;

import java.util.List;
import java.util.function.Function;

/**
 * Drives a persistent-container session. Unlike a one-shot {@code run --rm}, a session reuses a
 * long-lived container across invocations: it is created and set up once, then merely started and
 * attached to on later runs. The state observed by a probe selects the path:
 * <ul>
 *   <li>{@link ContainerState#ABSENT} &rarr; pull, build, create, then run the create-time and
 *   start-time lifecycle commands;</li>
 *   <li>{@link ContainerState#STOPPED} &rarr; start, then run the start-time lifecycle commands
 *   (the create-time commands are skipped, which is what makes them run exactly once);</li>
 *   <li>{@link ContainerState#RUNNING} &rarr; attach directly.</li>
 * </ul>
 * The attach-time lifecycle commands and the interactive attach itself run on every invocation. The
 * container is stopped — but kept — when the attach returns, so the next run resumes it quickly. The
 * {@code fresh} flag removes any existing container first, forcing a clean-slate recreate.
 */
public final class SessionLifecycle {

  /** Runs a command line, returning its exit code. An empty argument list is a no-op returning zero. */
  @FunctionalInterface
  public interface Executor {
    int execute(List<String> arguments, boolean debug);
  }

  /** Observes the current container state by running and interpreting the probe command line. */
  @FunctionalInterface
  public interface Probe {
    ContainerState state(List<String> probeArguments);
  }

  /**
   * The command lines for each step of a session. Any list may be empty, in which case that step is
   * skipped (e.g. {@code pull}/{@code build} for an image that needs neither).
   */
  public record Steps(
      List<String> probe,
      List<String> remove,
      List<String> pull,
      List<String> build,
      List<String> create,
      List<String> start,
      List<String> attach,
      List<List<String>> teardown) {
  }

  /**
   * The lifecycle command lines to execute at each phase. Each phase holds zero or more fully-formed
   * command lines (e.g. {@code docker exec <name> sh -c "<command>"}). Commands run in order.
   *
   * @param onCreate Run once, immediately after the container is created.
   * @param onStart  Run after every start, including the start implied by creation.
   * @param onAttach Run before every attach.
   */
  public record Lifecycle(
      List<List<String>> onCreate,
      List<List<String>> onStart,
      List<List<String>> onAttach) {

    /** A lifecycle with no commands, for commands that have none (shell, compose). */
    public static Lifecycle none() {
      return new Lifecycle(List.of(), List.of(), List.of());
    }
  }

  public static int run(
      final Steps steps,
      final Lifecycle lifecycle,
      final boolean fresh,
      final boolean debug,
      final Executor executor,
      final Probe probe) {
    if (fresh) {
      // The removal may fail simply because nothing exists yet; its exit code is irrelevant to the
      // clean-slate intent, so it is ignored and the session proceeds as if the container is absent.
      executor.execute(steps.remove(), debug);
    }
    final var state = fresh ? ContainerState.ABSENT : probe.state(steps.probe());

    if (ContainerState.ABSENT == state) {
      final var prepared = firstFailure(debug, executor,
          steps.pull(), steps.build(), steps.create());
      if (0 != prepared) {
        return prepared;
      }
      final var created = runLifecycle(lifecycle.onCreate(), debug, executor);
      if (0 != created) {
        return created;
      }
      final var started = runLifecycle(lifecycle.onStart(), debug, executor);
      if (0 != started) {
        return started;
      }
    } else if (ContainerState.STOPPED == state) {
      final var started = executor.execute(steps.start(), debug);
      if (0 != started) {
        return started;
      }
      final var hooks = runLifecycle(lifecycle.onStart(), debug, executor);
      if (0 != hooks) {
        return hooks;
      }
    }

    final var attached = runLifecycle(lifecycle.onAttach(), debug, executor);
    if (0 != attached) {
      return attached;
    }

    final var attachExitCode = executor.execute(steps.attach(), debug);
    // Tear down after the interactive attach returns. The default teardown stops — but keeps — the
    // container so the next run resumes instead of rebuilding; a fuller teardown may also remove it.
    // Teardown is best-effort: its exit codes must not mask the exit code of the work the user ran.
    for (final var command : steps.teardown()) {
      executor.execute(command, debug);
    }
    return attachExitCode;
  }

  private static int firstFailure(
      final boolean debug,
      final Executor executor,
      final List<String>... steps) {
    for (final var step : steps) {
      final var exitCode = executor.execute(step, debug);
      if (0 != exitCode) {
        return exitCode;
      }
    }
    return 0;
  }

  private static int runLifecycle(
      final List<List<String>> commands,
      final boolean debug,
      final Executor executor) {
    for (final var command : commands) {
      final var exitCode = executor.execute(command, debug);
      if (0 != exitCode) {
        return exitCode;
      }
    }
    return 0;
  }

  private SessionLifecycle() {
    // utility class
  }

}
