/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.model;

import wtf.metio.ilo.cli.Executables;

import java.util.List;

public interface CliExecutor<RUNTIME extends Runtime, CLI extends CliTool<?>> {

  CLI selectRuntime(RUNTIME runtime);

  default int execute(final List<String> arguments, final boolean debug) {
    return Executables.runAndWaitForExit(arguments, debug);
  }

}
