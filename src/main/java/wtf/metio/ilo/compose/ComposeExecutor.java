/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import wtf.metio.ilo.cli.AutoSelectRuntime;
import wtf.metio.ilo.cli.Executables;
import wtf.metio.ilo.model.ComposeCLI;

import java.util.List;

final class ComposeExecutor implements Compose.ComposeAPI {

  @Override
  public ComposeCLI selectRuntime(final ComposeRuntime runtime) {
    return AutoSelectRuntime.selectComposeRuntime(runtime);
  }

  @Override
  public int execute(final List<String> arguments, final boolean debug) {
    return Executables.runAndWaitForExit(arguments, debug);
  }

}
