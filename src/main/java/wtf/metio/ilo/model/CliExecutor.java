/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.model;

import wtf.metio.ilo.cli.ContainerState;
import wtf.metio.ilo.cli.Executables;
import wtf.metio.ilo.cli.SessionLifecycle;

import java.util.List;

public interface CliExecutor<RUNTIME extends Runtime<CLI>, CLI extends CliTool<OPTIONS>, OPTIONS extends Options> {

  CLI selectRuntime(RUNTIME runtime);

  default int execute(final List<String> arguments, final boolean debug) {
    return Executables.runAndWaitForExit(arguments, debug);
  }

  /**
   * Runs a command and returns its captured standard output, used to query the runtime (e.g. to list
   * the containers that belong to a project).
   *
   * @param arguments The command line to run.
   * @return The trimmed standard output.
   */
  default String capture(final List<String> arguments) {
    return Executables.runAndReadOutput(arguments.toArray(new String[0]));
  }

  /**
   * @return A probe that reports a container's state by running its probe command line and
   * interpreting the captured output.
   */
  default SessionLifecycle.Probe probe() {
    return arguments -> ContainerState.fromProbe(Executables.runAndReadOutput(arguments.toArray(new String[0])));
  }

}
