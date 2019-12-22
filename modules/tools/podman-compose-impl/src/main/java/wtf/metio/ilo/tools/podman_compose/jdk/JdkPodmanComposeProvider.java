/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.tools.podman_compose.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.podman_compose.PodmanComposeCli;
import wtf.metio.ilo.tools.podman_compose.PodmanComposeProvider;

import java.util.Optional;

public final class JdkPodmanComposeProvider implements PodmanComposeProvider {

  @Override
  public Optional<PodmanComposeCli> apply(final Executables executables) {
    return ExecutablePaths.of(Constants.PODMAN_COMPOSE_COMMAND)
        .map(path -> new JdkPodmanCompose(executables));
  }

}
