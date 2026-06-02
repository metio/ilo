/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.compose;

import wtf.metio.ilo.model.CliExecutor;

final class ComposeExecutor implements CliExecutor<ComposeRuntime, ComposeCLI, ComposeOptions> {

  @Override
  public ComposeCLI selectRuntime(final ComposeRuntime runtime) {
    return ComposeRuntime.autoSelect(runtime);
  }

}
