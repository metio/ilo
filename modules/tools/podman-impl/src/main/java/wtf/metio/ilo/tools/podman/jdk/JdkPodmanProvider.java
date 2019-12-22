/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.tools.podman.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.podman.PodmanCli;
import wtf.metio.ilo.tools.podman.PodmanProvider;

import java.util.Optional;

public final class JdkPodmanProvider implements PodmanProvider {

  @Override
  public Optional<PodmanCli> apply(final Executables executables) {
    return ExecutablePaths.of(Constants.PODMAN_COMMAND)
        .map(path -> new JdkPodman(executables));
  }

}
