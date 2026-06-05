/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import wtf.metio.ilo.model.CliTool;

import java.util.List;
import java.util.function.Function;

public interface ShellCLI extends CliTool<ShellOptions> {

  /**
   * Resolves how this runtime aligns the container user with the host user so files written into the
   * mounted project stay owned by the caller. Podman and nerdctl map the container's root to the host
   * user already and use a keep-id namespace for a non-root user; {@link Docker} overrides this to
   * account for its lack of a default user namespace.
   *
   * @param enabled    Whether {@code --update-remote-user-uid} is in effect.
   * @param remoteUser The container user, or {@code null} for the image's default (root) user.
   * @param capture    Runs a command line and returns its standard output, used to probe the runtime.
   * @return The mapping to apply.
   */
  default RemoteUserMapping remoteUserMapping(final boolean enabled, final String remoteUser,
      final Function<List<String>, String> capture) {
    return RemoteUserMapping.resolve(false, false, remoteUser, enabled);
  }

  /**
   * Whether this runtime supports pinning a keep-id user namespace to a specific UID/GID
   * ({@code --userns=keep-id:uid=…,gid=…}). Only podman does; the others use a plain keep-id, which
   * aligns a container user only when its UID already matches the host UID.
   *
   * @return Whether {@code keep-id:uid=…,gid=…} is supported.
   */
  default boolean supportsKeepIdUid() {
    return false;
  }

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

  /**
   * Builds a command that reports the host PID of the container's main process ({@code PID 1} inside
   * the container). When the keepalive is off there is no command marker to recognise the container's
   * own process in {@code top} output, and a runtime whose {@code top} reports host PIDs (Docker) shows
   * it at its host PID rather than {@code 1}; this lets the session ref-count exclude it anyway.
   *
   * @param options       The options to use.
   * @param containerName The reused name of the session's container.
   * @return The inspect command line yielding the main process's host PID.
   */
  List<String> mainPidArguments(ShellOptions options, String containerName);

}
