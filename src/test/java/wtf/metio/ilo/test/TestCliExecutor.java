/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.test;

import wtf.metio.ilo.cli.ContainerState;
import wtf.metio.ilo.cli.SessionLifecycle;
import wtf.metio.ilo.model.CliExecutor;
import wtf.metio.ilo.model.CliTool;
import wtf.metio.ilo.model.Options;
import wtf.metio.ilo.model.Runtime;

import java.util.*;

public abstract class TestCliExecutor<RUNTIME extends Runtime<CLI>, CLI extends CliTool<OPTIONS>, OPTIONS extends Options>
    implements CliExecutor<RUNTIME, CLI, OPTIONS> {

  private final List<List<String>> collectedArguments = new ArrayList<>(4);
  private final ArrayDeque<Integer> exitCodes = new ArrayDeque<>(4);
  private ContainerState probeState = ContainerState.ABSENT;
  private String captureOutput = "";
  private String processesOutput = "";
  private String inspectOutput = "";

  @Override
  public final int execute(final List<String> arguments, final boolean debug) {
    collectedArguments.add(arguments);
    return Optional.ofNullable(exitCodes.pollFirst()).orElse(0);
  }

  // Records the command like execute and captures nothing, so tests never spawn a real process for the
  // parallel (captured) lifecycle steps.
  @Override
  public final SessionLifecycle.CommandResult executeCaptured(final List<String> arguments, final boolean debug) {
    return new SessionLifecycle.CommandResult(execute(arguments, debug), "");
  }

  @Override
  public final String capture(final List<String> arguments) {
    // The session captures four kinds of listing: a 'top' command lists processes, an 'inspect' reports
    // the main process PID, a state probe (a name-filtered listing) reports the container's state, and
    // the stale sweep (a label-filtered listing) lists container names.
    if (arguments.contains("top")) {
      return processesOutput;
    }
    if (arguments.contains("inspect")) {
      return inspectOutput;
    }
    if (arguments.stream().anyMatch(argument -> argument.startsWith("name="))) {
      return probeStateWord();
    }
    return captureOutput;
  }

  // The probe captures a runtime's state word and ContainerState.fromProbe maps it back, so the state
  // set via probeState() round-trips through the same path the production code now uses (capture()
  // rather than a canned probe).
  private String probeStateWord() {
    return switch (probeState) {
      case ABSENT -> "";
      case RUNNING -> "running";
      case PAUSED -> "paused";
      case STOPPED -> "exited";
    };
  }

  /** Controls the container state the next session sees. */
  public final void probeState(final ContainerState state) {
    probeState = state;
  }

  /** Controls the output the next {@link #capture} call returns (e.g. a list of container names). */
  public final void captureOutput(final String output) {
    captureOutput = output;
  }

  /** Controls the {@code top} output the next process check returns (the in-container process table). */
  public final void processesOutput(final String output) {
    processesOutput = output;
  }

  /** Controls the {@code inspect} output the next main-PID lookup returns. */
  public final void inspectOutput(final String output) {
    inspectOutput = output;
  }

  /** Every command line passed to {@link #execute}, including the empty (skipped) ones, in order. */
  public final List<List<String>> collected() {
    return collectedArguments;
  }

  /** The non-empty command lines passed to {@link #execute}, in order — the steps actually run. */
  public final List<List<String>> executed() {
    return collectedArguments.stream().filter(arguments -> !arguments.isEmpty()).toList();
  }

  public final void exitCodes(final Integer... codes) {
    exitCodes.addAll(Arrays.asList(codes));
  }

}
