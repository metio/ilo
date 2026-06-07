/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.model;

import wtf.metio.ilo.cli.Executables;
import wtf.metio.ilo.cli.SessionLifecycle;

import java.util.List;

public interface CliExecutor<RUNTIME extends Runtime<CLI>, CLI extends CliTool<OPTIONS>, OPTIONS extends Options> {

  CLI selectRuntime(RUNTIME runtime);

  default int execute(final List<String> arguments, final boolean debug) {
    return Executables.runAndWaitForExit(arguments, debug);
  }

  /** Runs a command capturing its combined output, for the parallel steps of a session lifecycle. */
  default SessionLifecycle.CommandResult executeCaptured(final List<String> arguments, final boolean debug) {
    return Executables.runAndCapture(arguments, debug);
  }

  /**
   * @return A {@link SessionLifecycle.Executor} backed by this executor's {@link #execute} and
   * {@link #executeCaptured}, so a session both runs inheriting commands and captures its parallel ones.
   */
  default SessionLifecycle.Executor sessionExecutor() {
    return new SessionLifecycle.Executor() {
      @Override
      public int execute(final List<String> arguments, final boolean debug) {
        return CliExecutor.this.execute(arguments, debug);
      }

      @Override
      public SessionLifecycle.CommandResult executeCaptured(final List<String> arguments, final boolean debug) {
        return CliExecutor.this.executeCaptured(arguments, debug);
      }
    };
  }

  /**
   * Runs a command and returns its captured standard output, used to query the runtime (e.g. to list
   * the containers that belong to a project, or to probe a container's state).
   *
   * @param arguments The command line to run.
   * @return The trimmed standard output.
   */
  default String capture(final List<String> arguments) {
    return Executables.runAndReadOutput(arguments.toArray(new String[0]));
  }

}
