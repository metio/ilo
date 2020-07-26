/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.exec.Executables;
import wtf.metio.ilo.tools.Tools;

import java.util.List;

final class ShellExecutor implements Shell.ShellAPI {

  @Override
  public ShellCLI selectRuntime(final ShellRuntime runtime) {
    return Tools.selectShellRuntime(runtime);
  }

  @Override
  public int execute(final List<String> arguments) {
    return Executables.runAndWaitForExit(arguments);
  }

}
