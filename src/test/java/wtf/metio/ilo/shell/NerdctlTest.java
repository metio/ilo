/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
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
