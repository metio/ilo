/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.compose.ComposeRuntime;
import wtf.metio.ilo.model.Matcher;
import wtf.metio.ilo.shell.ShellRuntime;

import java.util.Optional;
import java.util.stream.Stream;

public final class Tools {

  private Tools() {
    // utility class
  }

  public static Optional<ShellCLI> detectedShellRuntime(final ShellRuntime runtime) {
    final var docker = new Docker();
    final var podman = new Podman();
    return autoSelect(runtime, docker, podman);
  }

  public static Optional<ComposeCLI> detectedComposeRuntime(final ComposeRuntime runtime) {
    final var dockerCompose = new DockerCompose();
    final var podmanCompose = new PodmanCompose();
    return autoSelect(runtime, dockerCompose, podmanCompose);
  }

  @SafeVarargs
  static <SHELL extends CliTool<?>> Optional<SHELL> autoSelect(
      final Matcher runtime,
      final SHELL... tools) {
    return Stream.of(tools)
        .filter(CliTool::exists)
        .filter(tool -> null == runtime || runtime.matches(tool.name()))
        .findFirst();
  }

}
