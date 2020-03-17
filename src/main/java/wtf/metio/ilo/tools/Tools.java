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
    final var podman = getPodmanCli(executables);
    final var docker = getDockerCli(executables);

    return Stream.of(podman, docker)
        .map(CliTool::name)
        .filter(ExecutablePaths::exists)
        .filter(tool -> null == runtime || runtime.matches(tool));
  }

  public static Stream<String> detectedComposeRuntime(final Executables executables, final ComposeRuntime runtime) {
    final var podmanCompose = getPodmanComposeCli(executables);
    final var dockerCompose = getDockerComposeCli(executables);

    return Stream.of(podmanCompose, dockerCompose)
        .map(CliTool::name)
        .filter(ExecutablePaths::exists)
        .filter(tool -> null == runtime || runtime.matches(tool));
  }

  private static PodmanCli getPodmanCli(final Executables executables) {
    return new JdkPodman(executables);
  }

  private static PodmanComposeCli getPodmanComposeCli(final Executables executables) {
    return new JdkPodmanCompose(executables);
  }

  private static DockerCli getDockerCli(final Executables executables) {
    return new JdkDocker(executables);
  }

  private static DockerComposeCli getDockerComposeCli(final Executables executables) {
    return new JdkDockerComposeCli(executables);
  }

  private static BuildahCli getBuildahCli(final Executables executables) {
    return new JdkBuildah(executables);
  }

  private static KubectlCli getKubectlCli(final Executables executables) {
    return new JdkKubectl(executables);
  }

}
