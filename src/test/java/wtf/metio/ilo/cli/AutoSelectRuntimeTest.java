/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.errors.NoMatchingRuntimeException;
import wtf.metio.ilo.tools.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static wtf.metio.ilo.cli.AutoSelectRuntime.selectComposeRuntime;
import static wtf.metio.ilo.cli.AutoSelectRuntime.selectShellRuntime;
import static wtf.metio.ilo.compose.ComposeRuntime.DOCKER_COMPOSE;
import static wtf.metio.ilo.compose.ComposeRuntime.PODMAN_COMPOSE;
import static wtf.metio.ilo.shell.ShellRuntime.DOCKER;
import static wtf.metio.ilo.shell.ShellRuntime.PODMAN;

@DisplayName("AutoSelectRuntime")
class AutoSelectRuntimeTest {

  @Nested
  @DisplayName("shell")
  class ShellRuntimesTest {

    @Test
    @DisplayName("podman is the first choice")
    void podman() {
      assumeTrue(new Podman().exists());
      assertEquals("podman", selectShellRuntime(null).name());
    }

    @Test
    @DisplayName("docker is the second choice")
    void docker() {
      assumeFalse(new Podman().exists());
      assumeTrue(new Docker().exists());
      assertEquals("docker", selectShellRuntime(null).name());
    }

    @Test
    @DisplayName("can force to use podman")
    void forcePodman() {
      assumeTrue(new Podman().exists());
      assertEquals("podman", selectShellRuntime(PODMAN).name());
    }

    @Test
    @DisplayName("can force to use docker")
    void forceDocker() {
      assumeTrue(new Docker().exists());
      assertEquals("docker", selectShellRuntime(DOCKER).name());
    }

    @Test
    @DisplayName("throws in case there are no shell runtimes available")
    void throwsWithoutAnyRuntime() {
      assumeFalse(new Podman().exists());
      assumeFalse(new Docker().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectShellRuntime(null));
    }

    @Test
    @DisplayName("throws in case in case podman is not installed but specified")
    void throwsPodman() {
      assumeFalse(new Podman().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectShellRuntime(PODMAN));
    }

    @Test
    @DisplayName("throws in case in case docker is not installed but specified")
    void throwsDocker() {
      assumeFalse(new Docker().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectShellRuntime(DOCKER));
    }

  }

  @Nested
  @DisplayName("compose")
  class ComposeRuntimesTest {

    @Test
    @DisplayName("docker-compose is the first choice")
    void dockerCompose() {
      assumeTrue(new DockerCompose().exists());
      assertEquals("docker-compose", selectComposeRuntime(null).name());
    }

    @Test
    @DisplayName("podman-compose is the second choice")
    void podmanCompose() {
      assumeFalse(new DockerCompose().exists());
      assumeTrue(new PodmanCompose().exists());
      assertEquals("podman-compose", selectComposeRuntime(null).name());
    }

    @Test
    @DisplayName("can force to use docker-compose")
    void forceDockerCompose() {
      assumeTrue(new DockerCompose().exists());
      assertEquals("docker-compose", selectComposeRuntime(DOCKER_COMPOSE).name());
    }

    @Test
    @DisplayName("can force to use podman-compose")
    void forcePodmanCompose() {
      assumeTrue(new PodmanCompose().exists());
      assertEquals("podman-compose", selectComposeRuntime(PODMAN_COMPOSE).name());
    }

    @Test
    @DisplayName("throws in case there are no compose runtimes available")
    void throwsWithoutAnyRuntime() {
      assumeFalse(new DockerCompose().exists());
      assumeFalse(new PodmanCompose().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectComposeRuntime(null));
    }

    @Test
    @DisplayName("throws in case in case docker-compose is not installed but specified")
    void throwsDockerCompose() {
      assumeFalse(new DockerCompose().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectComposeRuntime(DOCKER_COMPOSE));
    }

    @Test
    @DisplayName("throws in case in case podman-compose is not installed but specified")
    void throwsPodmanCompose() {
      assumeFalse(new PodmanCompose().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectComposeRuntime(PODMAN_COMPOSE));
    }

  }

}
