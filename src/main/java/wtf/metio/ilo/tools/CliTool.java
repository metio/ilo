/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.tools;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Most generic CLI tool.
 */
public interface CliTool {

  /**
   * @return The name of the CLI tool.
   */
  String name();

  /**
   * @return The version of the CLI tool.
   */
  Optional<String> version();

  /**
   * @return The absolute path to the CLI tool.
   */
  Optional<Path> path();

}
