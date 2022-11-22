/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wtf.metio.ilo.errors.NoMatchingRuntimeException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static wtf.metio.ilo.compose.ComposeRuntime.DOCKER_COMPOSE;
import static wtf.metio.ilo.compose.ComposeRuntime.PODMAN_COMPOSE;

@DisplayName("ComposeRuntime")
class ComposeRuntimeTest {

  @ParameterizedTest
  @DisplayName("defines compose runtime constants")
  @ValueSource(strings = {
    "PODMAN_COMPOSE",
    "DOCKER_COMPOSE",
    "DOCKER"
  })
  void shouldHaveRuntime(final String runtime) {
    assertNotNull(ComposeRuntime.valueOf(runtime));
  }

  @ParameterizedTest
  @DisplayName("supports aliases")
  @ValueSource(strings = {
    "podman-compose",
    "docker-compose",
    "docker",
    "pc",
    "dc",
    "d",
    "DOCKER-COMPOSE",
    "PODMAN-COMPOSE",
    "dOCkeR",
    "dOCkeR-cOMpOSe",
    "podMAN-compOSe"
  })
  void shouldSupportAlias(final String alias) {
    assertNotNull(ComposeRuntime.fromAlias(alias));
  }

  @Test
  @DisplayName("supports multiple runtimes")
  void shouldSupportMultipleRuntimes() {
    assertEquals(3, ComposeRuntime.values().length);
  }

  @Test
  @DisplayName("docker is the first choice")
  void docker() {
    assertEquals("docker", ComposeRuntime.values()[0].cli().name());
  }

  @Test
  @DisplayName("docker-compose is the second choice")
  void dockerCompose() {
    assertEquals("docker-compose", ComposeRuntime.values()[1].cli().name());
  }

  @Test
  @DisplayName("podman-compose is the third choice")
  void podmanCompose() {
    assertEquals("podman-compose", ComposeRuntime.values()[2].cli().name());
  }

  @Test
  @DisplayName("docker is the first choice")
  void autoSelectDocker() {
    assumeTrue(new DockerCompose2().exists());
    assertEquals("docker", ComposeRuntime.autoSelect(null).name());
  }

  @Test
  @DisplayName("docker-compose is the second choice")
  void autoSelectDockerCompose() {
    assumeFalse(new DockerCompose2().exists());
    assumeTrue(new DockerCompose().exists());
    assertEquals("podman-compose", ComposeRuntime.autoSelect(null).name());
  }

  @Test
  @DisplayName("podman-compose is the third choice")
  void autoSelectPodmanCompose() {
    assumeFalse(new DockerCompose2().exists());
    assumeFalse(new DockerCompose().exists());
    assumeTrue(new PodmanCompose().exists());
    assertEquals("podman-compose", ComposeRuntime.autoSelect(null).name());
  }

  @Test
  @DisplayName("can force to use docker-compose")
  void forceDockerCompose() {
    assumeTrue(new DockerCompose().exists());
    assertEquals("docker-compose", ComposeRuntime.autoSelect(DOCKER_COMPOSE).name());
  }

  @Test
  @DisplayName("can force to use podman-compose")
  void forcePodmanCompose() {
    assumeTrue(new PodmanCompose().exists());
    assertEquals("podman-compose", ComposeRuntime.autoSelect(PODMAN_COMPOSE).name());
  }

  @Test
  @DisplayName("can force to use docker")
  void forceDockerCompose2() {
    assumeTrue(new DockerCompose2().exists());
    assertEquals("docker", ComposeRuntime.autoSelect(ComposeRuntime.DOCKER).name());
  }

  @Test
  @DisplayName("throws in case there are no compose runtimes available")
  void throwsWithoutAnyRuntime() {
    assumeFalse(new DockerCompose().exists());
    assumeFalse(new PodmanCompose().exists());
    assumeFalse(new DockerCompose2().exists());
    assertThrows(NoMatchingRuntimeException.class, () -> ComposeRuntime.autoSelect(null));
  }

  @Test
  @DisplayName("throws in case in case docker-compose is not installed but specified")
  void throwsDockerCompose() {
    assumeFalse(new DockerCompose().exists());
    assertThrows(NoMatchingRuntimeException.class, () -> ComposeRuntime.autoSelect(DOCKER_COMPOSE));
  }

  @Test
  @DisplayName("throws in case in case podman-compose is not installed but specified")
  void throwsPodmanCompose() {
    assumeFalse(new PodmanCompose().exists());
    assertThrows(NoMatchingRuntimeException.class, () -> ComposeRuntime.autoSelect(PODMAN_COMPOSE));
  }

  @Test
  @DisplayName("throws in case in case docker is not installed but specified")
  void throwsDockerCompose2() {
    assumeFalse(new DockerCompose2().exists());
    assertThrows(NoMatchingRuntimeException.class, () -> ComposeRuntime.autoSelect(ComposeRuntime.DOCKER));
  }

}
