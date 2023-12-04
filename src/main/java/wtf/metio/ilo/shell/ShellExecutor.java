/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.model.CliExecutor;

final class ShellExecutor implements CliExecutor<ShellRuntime, ShellCLI, ShellOptions> {

  @Override
  public ShellCLI selectRuntime(final ShellRuntime runtime) {
    return ShellRuntime.autoSelect(runtime);
  }

}
