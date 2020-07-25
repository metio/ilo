/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.exec.Executables;

import java.util.List;

/**
 * Most generic CLI tool.
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
   * @return The command line to execute.
   */
  List<String> runArguments(OPTIONS options);

}
