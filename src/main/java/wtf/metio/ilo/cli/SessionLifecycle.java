/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import java.util.List;
import java.util.function.IntSupplier;

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

    final var prepared = prepare(state, steps, lifecycle, debug, executor);
    if (0 != prepared) {
      return prepared;
    }
    final var attached = runLifecycle(lifecycle.onAttach(), debug, executor);
    if (0 != attached) {
      return attached;
    }

    final var attachExitCode = executor.execute(steps.attach(), debug);
    // Tear down after the interactive attach returns. The default teardown stops — but keeps — the
    // container so the next run resumes instead of rebuilding; a fuller teardown may also remove it.
    // Teardown is best-effort: its exit codes must not mask the exit code of the work the user ran.
    steps.teardown().forEach(command -> executor.execute(command, debug));
    return attachExitCode;
  }

  // Brings the container to a ready-to-attach state for the observed state: an absent container is
  // pulled, built and created (then its create- and start-time lifecycle commands run), a stopped one
  // is started (then its start-time commands run), a running one needs nothing. Returns the first
  // non-zero exit code encountered, or zero.
  private static int prepare(
      final ContainerState state,
      final Steps steps,
      final Lifecycle lifecycle,
      final boolean debug,
      final Executor executor) {
    if (ContainerState.ABSENT == state) {
      return firstNonZero(
          () -> executor.execute(steps.pull(), debug),
          () -> executor.execute(steps.build(), debug),
          () -> executor.execute(steps.create(), debug),
          () -> runLifecycle(lifecycle.onCreate(), debug, executor),
          () -> runLifecycle(lifecycle.onStart(), debug, executor));
    }
    if (ContainerState.STOPPED == state) {
      return firstNonZero(
          () -> executor.execute(steps.start(), debug),
          () -> runLifecycle(lifecycle.onStart(), debug, executor));
    }
    return 0;
  }

  // Evaluates each step in order until one yields a non-zero exit code, which it returns; otherwise
  // zero. The steps are lazy, so a failing step short-circuits the rest.
  private static int firstNonZero(final IntSupplier... steps) {
    for (final var step : steps) {
      final var exitCode = step.getAsInt();
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
