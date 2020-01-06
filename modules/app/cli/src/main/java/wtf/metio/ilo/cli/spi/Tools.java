/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.cli.spi;

import wtf.metio.ilo.cli.usecases.HandleErrors;
import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.tools.api.CliTool;
import wtf.metio.ilo.tools.buildah.BuildahCli;
import wtf.metio.ilo.tools.buildah.BuildahProvider;
import wtf.metio.ilo.tools.docker.DockerCli;
import wtf.metio.ilo.tools.docker.DockerProvider;
import wtf.metio.ilo.tools.docker_compose.DockerComposeCli;
import wtf.metio.ilo.tools.docker_compose.DockerComposeProvider;
import wtf.metio.ilo.tools.kubectl.KubectlCli;
import wtf.metio.ilo.tools.kubectl.KubectlProvider;
import wtf.metio.ilo.tools.podman.PodmanCli;
import wtf.metio.ilo.tools.podman.PodmanProvider;
import wtf.metio.ilo.tools.podman_compose.PodmanComposeCli;
import wtf.metio.ilo.tools.podman_compose.PodmanComposeProvider;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Tools {

  private static final List<String> SUPPORTED_RUNTIMES = List.of("podman", "docker");

  private Tools() {
    // utility class
  }

  public static Stream<? extends CliTool> detectedTools(final Executables executables) {
    final var podman = getPodmanCli(executables);
    final var podmanCompose = getPodmanComposeCli(executables);
    final var buildah = getBuildahCli(executables);
    final var kubectl = getKubectlCli(executables);
    final var docker = getDockerCli(executables);
    final var dockerCompose = getDockerComposeCli(executables);

    final var tools = Stream.of(podman, podmanCompose, buildah, kubectl, docker, dockerCompose)
        .flatMap(Optional::stream)
        .collect(Collectors.toList());

    if (tools.isEmpty()) {
      HandleErrors.handleMissingTools();
    }
    if (tools.stream().map(CliTool::name).map(String::toLowerCase).noneMatch(SUPPORTED_RUNTIMES::contains)) {
      HandleErrors.handleMissingRuntime(SUPPORTED_RUNTIMES);
    }

    return tools.stream();
  }

  private static Optional<PodmanCli> getPodmanCli(final Executables executables) {
    return ServiceLoader.load(PodmanProvider.class).findFirst().flatMap(provider -> provider.apply(executables));
  }

  private static Optional<PodmanComposeCli> getPodmanComposeCli(final Executables executables) {
    return ServiceLoader.load(PodmanComposeProvider.class).findFirst().flatMap(provider -> provider.apply(executables));
  }

  private static Optional<BuildahCli> getBuildahCli(final Executables executables) {
    return ServiceLoader.load(BuildahProvider.class).findFirst().flatMap(provider -> provider.apply(executables));
  }

  private static Optional<KubectlCli> getKubectlCli(final Executables executables) {
    return ServiceLoader.load(KubectlProvider.class).findFirst().flatMap(provider -> provider.apply(executables));
  }

  private static Optional<DockerCli> getDockerCli(final Executables executables) {
    return ServiceLoader.load(DockerProvider.class).findFirst().flatMap(provider -> provider.apply(executables));
  }

  private static Optional<DockerComposeCli> getDockerComposeCli(final Executables executables) {
    return ServiceLoader.load(DockerComposeProvider.class).findFirst().flatMap(provider -> provider.apply(executables));
  }

}
