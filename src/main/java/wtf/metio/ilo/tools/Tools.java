/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.exec.ExecutablePaths;
import wtf.metio.ilo.exec.Executables;
import wtf.metio.ilo.model.ComposeRuntime;
import wtf.metio.ilo.model.Runtime;

import java.util.stream.Stream;

public final class Tools {

  private Tools() {
    // utility class
  }

  public static Stream<String> detectedShellRuntime(final Executables executables, final Runtime runtime) {
    final var docker = getDockerCli(executables);
    final var podman = getPodmanCli(executables);

    return Stream.of(docker, podman)
        .map(CliTool::name)
        .filter(ExecutablePaths::exists)
        .filter(tool -> null == runtime || runtime.matches(tool));
  }

  public static Stream<String> detectedComposeRuntime(final Executables executables, final ComposeRuntime runtime) {
    final var dockerCompose = getDockerComposeCli(executables);
    final var podmanCompose = getPodmanComposeCli(executables);

    return Stream.of(dockerCompose, podmanCompose)
        .map(CliTool::name)
        .filter(ExecutablePaths::exists)
        .filter(tool -> null == runtime || runtime.matches(tool));
  }

  private static PodmanCLI getPodmanCli(final Executables executables) {
    return new Podman(executables);
  }

  private static PodmanComposeCLI getPodmanComposeCli(final Executables executables) {
    return new PodmanCompose(executables);
  }

  private static DockerCLI getDockerCli(final Executables executables) {
    return new Docker(executables);
  }

  private static DockerComposeCLI getDockerComposeCli(final Executables executables) {
    return new DockerCompose(executables);
  }

  private static BuildahCLI getBuildahCli(final Executables executables) {
    return new Buildah(executables);
  }

  private static KubectlCLI getKubectlCli(final Executables executables) {
    return new Kubectl(executables);
  }

}
