/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;

@DisplayName("Nerdctl")
class NerdctlTest extends DockerLikeTCK {

  @Override
  public ShellCLI tool() {
    return new Nerdctl();
  }

  @Override
  protected ShellOptions options() {
    return new ShellOptions();
  }

  @Override
  protected String name() {
    return "nerdctl";
  }

}
