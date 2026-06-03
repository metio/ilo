/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.compose;

import wtf.metio.ilo.model.CliTool;

import java.util.List;

public interface ComposeCLI extends CliTool<ComposeOptions> {

  /**
   * Builds a command that lists the processes running in the attached service's container, used to
   * tell whether another terminal still has the session open. With the keepalive override on, the
   * service runs the keepalive as PID 1, so an attached session is any other process — detected from
   * the runtime alone, without host-side bookkeeping.
   *
   * @param options The options to use; its {@code service} selects the container to inspect.
   * @return The process-listing command line.
   */
  List<String> processesArguments(ComposeOptions options);

}
