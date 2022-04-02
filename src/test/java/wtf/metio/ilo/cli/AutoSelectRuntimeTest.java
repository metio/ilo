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
import wtf.metio.ilo.tools.LXD;
import wtf.metio.ilo.tools.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static wtf.metio.ilo.cli.AutoSelectRuntime.selectComposeRuntime;
import static wtf.metio.ilo.cli.AutoSelectRuntime.selectShellRuntime;
import static wtf.metio.ilo.compose.ComposeRuntime.*;
import static wtf.metio.ilo.shell.ShellRuntime.*;

@DisplayName("AutoSelectRuntime")
class AutoSelectRuntimeTest {

  @Nested
  @DisplayName("shell")
  class ShellRuntimes {

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
    @DisplayName("lxd is the third choice")
    void lxd() {
      assumeFalse(new Podman().exists());
      assumeFalse(new Docker().exists());
      assumeTrue(new LXD().exists());
      assertEquals("lxc", selectShellRuntime(null).name());
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
    @DisplayName("can force to use lxd")
    void forceLxd() {
      assumeTrue(new LXD().exists());
      assertEquals("lxc", selectShellRuntime(LXD).name());
    }

    @Test
    @DisplayName("throws in case there are no shell runtimes available")
    void throwsWithoutAnyRuntime() {
      assumeFalse(new Podman().exists());
      assumeFalse(new Docker().exists());
      assumeFalse(new LXD().exists());
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

    @Test
    @DisplayName("throws in case in case lxd is not installed but specified")
    void throwsLXD() {
      assumeFalse(new LXD().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectShellRuntime(LXD));
    }

  }

  @Nested
  @DisplayName("compose")
  class ComposeRuntimes {

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
    @DisplayName("pods-compose is the third choice")
    void podsCompose() {
      assumeFalse(new DockerCompose().exists());
      assumeFalse(new PodmanCompose().exists());
      assumeTrue(new PodsCompose().exists());
      assertEquals("pods-compose", selectComposeRuntime(null).name());
    }

    @Test
    @DisplayName("vagrant is the fourth choice")
    void vagrant() {
      assumeFalse(new DockerCompose().exists());
      assumeFalse(new PodmanCompose().exists());
      assumeFalse(new PodsCompose().exists());
      assumeTrue(new Vagrant().exists());
      assertEquals("vagrant", selectComposeRuntime(null).name());
    }

    @Test
    @DisplayName("footloose is the fifth choice")
    void footloose() {
      assumeFalse(new DockerCompose().exists());
      assumeFalse(new PodmanCompose().exists());
      assumeFalse(new PodsCompose().exists());
      assumeFalse(new Vagrant().exists());
      assumeTrue(new Footloose().exists());
      assertEquals("footloose", selectComposeRuntime(null).name());
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
    @DisplayName("can force to use pods-compose")
    void forcePodsCompose() {
      assumeTrue(new PodmanCompose().exists());
      assertEquals("pods-compose", selectComposeRuntime(PODS_COMPOSE).name());
    }

    @Test
    @DisplayName("can force to use vagrant")
    void forceVagrant() {
      assumeTrue(new Vagrant().exists());
      assertEquals("vagrant", selectComposeRuntime(VAGRANT).name());
    }

    @Test
    @DisplayName("can force to use footloose")
    void forceFootloose() {
      assumeTrue(new Footloose().exists());
      assertEquals("footloose", selectComposeRuntime(FOOTLOOSE).name());
    }

    @Test
    @DisplayName("throws in case there are no compose runtimes available")
    void throwsWithoutAnyRuntime() {
      assumeFalse(new DockerCompose().exists());
      assumeFalse(new PodmanCompose().exists());
      assumeFalse(new PodsCompose().exists());
      assumeFalse(new Vagrant().exists());
      assumeFalse(new Footloose().exists());
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

    @Test
    @DisplayName("throws in case in case pods-compose is not installed but specified")
    void throwsPodsCompose() {
      assumeFalse(new PodsCompose().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectComposeRuntime(PODS_COMPOSE));
    }

    @Test
    @DisplayName("throws in case in case vagrant is not installed but specified")
    void throwsVagrant() {
      assumeFalse(new Vagrant().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectComposeRuntime(VAGRANT));
    }

    @Test
    @DisplayName("throws in case in case footloose is not installed but specified")
    void throwsFootloose() {
      assumeFalse(new Footloose().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectComposeRuntime(FOOTLOOSE));
    }

  }

}
