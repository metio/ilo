/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Drives a persistent-container session. Unlike a one-shot {@code run --rm}, a session reuses a
 * long-lived container across invocations: it is created and set up once, then merely started and
 * attached to on later runs. The state observed by the caller selects the path:
 * <ul>
 *   <li>{@link ContainerState#ABSENT} &rarr; pull, build, create, then run the create-time and
 *   start-time lifecycle commands;</li>
 *   <li>{@link ContainerState#STOPPED} &rarr; start, then run the start-time lifecycle commands
 *   (the create-time commands are skipped, which is what makes them run exactly once);</li>
 *   <li>{@link ContainerState#RUNNING} &rarr; attach directly.</li>
 * </ul>
 * The attach-time lifecycle commands and the interactive attach itself run on every invocation. When
 * the attach returns the container is torn down by the (lazily computed) teardown step — typically
 * stopped but kept, so the next run resumes it quickly — unless that step observes a reason to leave
 * it running. The {@code fresh} flag removes any existing container first, forcing a clean-slate
 * recreate.
 */
public final class SessionLifecycle {

  /** Runs a command line, returning its exit code. An empty argument list is a no-op returning zero. */
  public interface Executor {

    int execute(List<String> arguments, boolean debug);

    /**
     * Runs a command line capturing its (combined) output instead of inheriting the terminal, so the
     * caller can print it as one block. Used for the parallel steps of a phase, where inheriting would
     * let concurrent commands interleave on the terminal. The default keeps the inheriting behavior and
     * captures nothing, which suits in-process test doubles.
     */
    default CommandResult executeCaptured(final List<String> arguments, final boolean debug) {
      return new CommandResult(execute(arguments, debug), "");
    }
  }

  /** The exit code and captured output of a command run via {@link Executor#executeCaptured}. */
  public record CommandResult(int exitCode, String output) {
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
      List<String> remove,
      List<String> pull,
      List<String> build,
      List<String> create,
      List<String> start,
      List<String> attach,
      Supplier<List<List<String>>> teardown) {
  }

  /**
   * The lifecycle commands to execute at each phase. Each phase is an ordered list of steps; a step is
   * a group of command lines that run together — a single command runs on its own, several commands
   * run in parallel (the devcontainer object-form lifecycle semantics). Steps within a phase run in
   * order, stopping at the first failure.
   *
   * @param onCreate Run once, immediately after the container is created.
   * @param onStart  Run after every start, including the start implied by creation.
   * @param onAttach Run before every attach.
   */
  public record Lifecycle(
      List<List<List<String>>> onCreate,
      List<List<List<String>>> onStart,
      List<List<List<String>>> onAttach) {

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
      final ContainerState observedState) {
    if (fresh) {
      // The removal may fail simply because nothing exists yet; its exit code is irrelevant to the
      // clean-slate intent, so it is ignored and the session proceeds as if the container is absent.
      executor.execute(steps.remove(), debug);
    }
    final var state = fresh ? ContainerState.ABSENT : observedState;

    final var prepared = prepare(state, steps, lifecycle, debug, executor);
    if (0 != prepared) {
      return prepared;
    }
    final var attached = runLifecycle(lifecycle.onAttach(), debug, executor);
    if (0 != attached) {
      return attached;
    }

    final var attachExitCode = executor.execute(steps.attach(), debug);
    // Tear down after the interactive attach returns. The teardown is computed now, not up front, so
    // it can observe the container's current state — e.g. leaving it running while another terminal
    // still has it open, and otherwise stopping (but keeping) it so the next run resumes quickly.
    // Teardown is best-effort: its exit codes must not mask the exit code of the work the user ran.
    steps.teardown().get().forEach(command -> executor.execute(command, debug));
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

  // Runs each step of a phase in order, stopping at the first step that fails.
  private static int runLifecycle(
      final List<List<List<String>>> steps,
      final boolean debug,
      final Executor executor) {
    for (final var step : steps) {
      final var exitCode = runStep(step, debug, executor);
      if (0 != exitCode) {
        return exitCode;
      }
    }
    return 0;
  }

  // Runs one step's command lines: a single command runs inline (inheriting the terminal), several run
  // in parallel. The parallel commands run on virtual threads (so the blocking subprocess waits do not
  // tie up the shared common pool) with their output captured; each captured block is printed whole
  // afterwards so concurrent commands do not interleave on the terminal. All are awaited and the first
  // non-zero exit code is surfaced, so a failure in any command fails the step.
  private static int runStep(
      final List<List<String>> commands,
      final boolean debug,
      final Executor executor) {
    if (1 == commands.size()) {
      return executor.execute(commands.get(0), debug);
    }
    try (final var pool = Executors.newVirtualThreadPerTaskExecutor()) {
      final var futures = commands.stream()
          .map(command -> CompletableFuture.supplyAsync(() -> executor.executeCaptured(command, debug), pool))
          .toList();
      final var results = futures.stream().map(SessionLifecycle::await).toList();
      results.stream()
          .map(CommandResult::output)
          .filter(output -> !output.isEmpty())
          .forEach(System.out::print);
      return results.stream().map(CommandResult::exitCode).filter(exitCode -> 0 != exitCode).findFirst().orElse(0);
    }
  }

  // Awaits a captured command, unwrapping the CompletionException that join() raises on failure so a
  // BusinessException keeps its identity — and therefore its mapped exit code — rather than reaching the
  // top-level handler as a generic wrapper.
  private static CommandResult await(final CompletableFuture<CommandResult> future) {
    try {
      return future.join();
    } catch (final CompletionException exception) {
      if (exception.getCause() instanceof final RuntimeException cause) {
        throw cause;
      }
      throw exception;
    }
  }

  private SessionLifecycle() {
    // utility class
  }

}
