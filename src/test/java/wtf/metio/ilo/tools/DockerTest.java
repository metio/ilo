/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import org.junit.jupiter.api.DisplayName;
import wtf.metio.ilo.model.ShellCLI;

@DisplayName("Docker")
class DockerTest extends DockerPodmanTCK {

  @Override
  public ShellCLI tool() {
    return new Docker();
  }

  @Override
  protected String name() {
    return "docker";
  }

}
