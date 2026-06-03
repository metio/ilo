/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import wtf.metio.ilo.model.CliTool;

import java.util.List;

public interface ShellCLI extends CliTool<ShellOptions> {

  /**
   * Builds a non-interactive {@code exec} into the running container, used to run an in-container
   * lifecycle command. The command is passed verbatim — it is not expanded on the host, so any
   * variables or globs in it are resolved inside the container where they are meant to apply.
   *
   * @param options       The options to use.
   * @param containerName The reused name of the session's container.
   * @param command       The command line to run inside the container.
   * @return The {@code exec} command line.
   */
  List<String> execArguments(ShellOptions options, String containerName, List<String> command);

  /**
   * Builds a command that lists the names of this project's stopped, ilo-managed containers, so that
   * stale ones left over from an earlier configuration can be removed. Running containers are
   * excluded so a session active in another terminal is never torn down.
   *
   * @param options    The options to use.
   * @param projectDir The absolute project directory, matched against the {@code ilo.project} label.
   * @return The listing command line.
   */
  List<String> staleContainersArguments(ShellOptions options, String projectDir);

  /**
   * Builds a command that lists the processes running inside the session's container, used to tell
   * whether another terminal still has it open. The container's keepalive runs as PID 1 with its
   * sleep as a direct child; every attached session is an {@code exec}'d process that is neither, so
   * an open session is detected from the runtime alone, without any host-side bookkeeping.
   *
   * @param options       The options to use.
   * @param containerName The reused name of the session's container.
   * @return The process-listing command line.
   */
  List<String> processesArguments(ShellOptions options, String containerName);

}
