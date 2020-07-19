/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.exec.ExecutablePaths;
import wtf.metio.ilo.exec.Executables;

import java.nio.file.Path;
import java.util.Optional;

public final class Buildah implements BuildahCLI {

  private final Executables executables;

  public Buildah(final Executables executables) {
    this.executables = executables;
  }

  @Override
  public Optional<String> version() {
    return executables.runAndReadOutput(Constants.BUILDAH_COMMAND, Constants.VERSION_FLAG)
        .map(output -> output.replace("buildah version", ""))
        .map(output -> output.substring(0, output.indexOf("(") - 1))
        .map(String::strip);
  }

  @Override
  public Optional<Path> path() {
    return ExecutablePaths.of(Constants.BUILDAH_COMMAND);
  }

}
