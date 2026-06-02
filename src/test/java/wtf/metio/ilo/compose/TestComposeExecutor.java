/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.compose;

import wtf.metio.ilo.test.TestCliExecutor;

class TestComposeExecutor extends TestCliExecutor<ComposeRuntime, ComposeCLI, ComposeOptions> {

  @Override
  public ComposeCLI selectRuntime(final ComposeRuntime runtime) {
    return runtime.cli();
  }

}
