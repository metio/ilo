/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.model;

import wtf.metio.ilo.cli.Executables;
import wtf.metio.ilo.compose.ComposeOptions;

import java.util.List;

/**
 * CLI tools are used by 'ilo' in order to do most of its work. This interface represents such a tool.
 */
public interface CliTool<OPTIONS> {

  /**
   * @return The name of the CLI tool.
   */
  String name();

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
