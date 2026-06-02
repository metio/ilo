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

  @Override
  public final int execute(final List<String> arguments, final boolean debug) {
    collectedArguments.add(arguments);
    return Optional.ofNullable(exitCodes.pollFirst()).orElse(0);
  }

  @Override
  public final SessionLifecycle.Probe probe() {
    return _ -> probeState;
  }

  @Override
  public final String capture(final List<String> arguments) {
    return captureOutput;
  }

  /** Controls the container state the next session sees. */
  public final void probeState(final ContainerState state) {
    probeState = state;
  }

  /** Controls the output the next {@link #capture} call returns (e.g. a list of container names). */
  public final void captureOutput(final String output) {
    captureOutput = output;
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
