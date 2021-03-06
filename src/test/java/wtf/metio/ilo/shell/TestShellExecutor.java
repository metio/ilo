/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.test.TestCliExecutor;

class TestShellExecutor extends TestCliExecutor<ShellRuntime, ShellCLI> {

  @Override
  public ShellCLI selectRuntime(final ShellRuntime runtime) {
    return ShellRuntimes.allRuntimes().stream()
      .filter(cli -> runtime.matches(cli.name()))
      .findFirst()
      .orElseThrow(IllegalArgumentException::new);
  }

}
