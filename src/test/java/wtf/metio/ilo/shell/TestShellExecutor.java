/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.test.TestCliExecutor;

class TestShellExecutor extends TestCliExecutor<ShellRuntime, ShellCLI, ShellOptions> {

  @Override
  public ShellCLI selectRuntime(final ShellRuntime runtime) {
    return runtime.cli();
  }

}
