/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.model;

import wtf.metio.ilo.cli.Executables;

import java.util.List;

/**
 * CLI tools are used by 'ilo' in order to do most of its work. This interface represents such a tool.
 *
 * <p>A tool builds the command lines for a persistent-container session: the container is created and
 * prepared once, then started and attached to on later runs. Each method returns the arguments for one
 * step; an empty list means the step is skipped. The {@code containerName} is the stable, reused name
 * derived for the current project and image.</p>
 */
public interface CliTool<OPTIONS extends Options> {

  /**
   * @return The name of the CLI tool.
   */
  String name();

  /**
   * @return The CLI subcommand to use.
   */
  default String command() {
    return "";
  }

  /**
   * @return Whether this CLI tool is installed and executable.
   */
  default boolean exists() {
    return Executables.of(name()).isPresent();
  }

  /**
   * @param options The options to use.
   * @return The command line for the 'pull' step.
   */
  List<String> pullArguments(OPTIONS options);

  /**
   * @param options The options to use.
   * @return The command line for the 'build' step.
   */
  List<String> buildArguments(OPTIONS options);

  /**
   * @param options       The options to use.
   * @param containerName The reused name of the session's container.
   * @return The command line that reports the container's state, e.g. {@code docker ps}.
   */
  default List<String> probeArguments(final OPTIONS options, final String containerName) {
    return List.of();
  }

  /**
   * @param options       The options to use.
   * @param containerName The reused name of the session's container.
   * @return The command line that removes the container, used to force a clean-slate recreate.
   */
  default List<String> removeArguments(final OPTIONS options, final String containerName) {
    return List.of();
  }

  /**
   * @param options       The options to use.
   * @param containerName The reused name of the session's container.
   * @return The command line that creates and starts the long-lived container.
   */
  default List<String> createArguments(final OPTIONS options, final String containerName) {
    return List.of();
  }

  /**
   * @param options       The options to use.
   * @param containerName The reused name of the session's container.
   * @return The command line that starts the existing, stopped container.
   */
  default List<String> startArguments(final OPTIONS options, final String containerName) {
    return List.of();
  }

  /**
   * @param options       The options to use.
   * @param containerName The reused name of the session's container.
   * @return The command line that attaches to the container, e.g. an interactive {@code exec}.
   */
  default List<String> attachArguments(final OPTIONS options, final String containerName) {
    return List.of();
  }

  /**
   * @param options       The options to use.
   * @param containerName The reused name of the session's container.
   * @return The command line that stops — but keeps — the container after the attach returns.
   */
  default List<String> stopArguments(final OPTIONS options, final String containerName) {
    return List.of();
  }

  /**
   * @param options The options to use.
   * @return The command line that removes the built image during a full teardown.
   */
  default List<String> cleanupArguments(final OPTIONS options) {
    return List.of();
  }

}
