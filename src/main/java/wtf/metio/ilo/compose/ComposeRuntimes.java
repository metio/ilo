/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import wtf.metio.ilo.tools.DockerCompose;
import wtf.metio.ilo.tools.DockerCompose2;
import wtf.metio.ilo.tools.PodmanCompose;

import java.util.List;

public final class ComposeRuntimes {

  public static List<ComposeCLI> allRuntimes() {
    return List.of(
      new DockerCompose(),
      new PodmanCompose(),
      new DockerCompose2());
  }

  private ComposeRuntimes() {
    // factory class
  }

}
