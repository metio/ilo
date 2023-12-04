/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.model;

import wtf.metio.ilo.cli.Executables;

import java.util.List;

/**
 * CLI tools are used by 'ilo' in order to do most of its work. This interface represents such a tool.
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
   * @param options The options to use.
   * @return The command line for the 'run' step.
   */
  List<String> runArguments(OPTIONS options);

  /**
   * @param options The options to use.
   * @return The command line for the 'cleanup' step.
   */
  List<String> cleanupArguments(OPTIONS options);

}
