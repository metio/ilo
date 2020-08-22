/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.factories;

import wtf.metio.ilo.model.ComposeCLI;
import wtf.metio.ilo.tools.*;

import java.util.List;

// TODO: move to compose package?
public final class ComposeRuntimes {

  public static List<ComposeCLI> allRuntimes() {
    final var dockerCompose = new DockerCompose();
    final var podmanCompose = new PodmanCompose();
    final var podsCompose = new PodsCompose();
    final var vagrant = new Vagrant();
    final var footloose = new Footloose();
    return List.of(podmanCompose, dockerCompose, podsCompose, vagrant, footloose);
  }

  private ComposeRuntimes() {
    // factory class
  }

}
