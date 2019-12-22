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

import java.nio.file.Path;
import java.util.Optional;

public final class JdkPodman implements PodmanCli {

  private final Executables executables;

  public JdkPodman(final Executables executables) {
    this.executables = executables;
  }

  @Override
  public Optional<String> version() {
    return executables.runAndReadOutput(Constants.PODMAN_COMMAND, Constants.VERSION_FLAG)
        .map(output -> output.replace("podman version", ""))
        .map(String::strip);
  }

  @Override
  public Optional<Path> path() {
    return ExecutablePaths.of(Constants.PODMAN_COMMAND);
  }

}
