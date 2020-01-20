/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.exec.ExecutablePaths;
import wtf.metio.ilo.exec.Executables;

import java.nio.file.Path;
import java.util.Optional;

public final class JdkKubectl implements KubectlCli {

  private final Executables executables;

  public JdkKubectl(final Executables executables) {
    this.executables = executables;
  }

  @Override
  public Optional<String> version() {
    return executables.runAndReadOutput(Constants.KUBECTL_COMMAND,
        "version",
        "--short=true",
        "--client=true")
        .map(output -> output.replace("Client Version: v", ""))
        .map(String::strip);
  }

  @Override
  public Optional<Path> path() {
    return ExecutablePaths.of(Constants.KUBECTL_COMMAND);
  }

}