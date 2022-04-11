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
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import wtf.metio.ilo.compose.ComposeRuntime;
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
import static wtf.metio.ilo.shell.ShellRuntime.*;

@DisplayName("AutoSelectRuntime")
@ExtendWith(SystemStubsExtension.class)
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
    @DisplayName("nerdctl is the second choice")
    void nerdctl() {
      assumeFalse(new Podman().exists());
      assumeTrue(new Nerdctl().exists());
      assertEquals("nerdctl", selectShellRuntime(null).name());
    }

    @Test
    @DisplayName("docker is the third choice")
    void docker() {
      assumeFalse(new Podman().exists());
      assumeFalse(new Nerdctl().exists());
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
    @DisplayName("can force to use podman with environment variable")
    void forcePodmanWithEnvVariable(final EnvironmentVariables environmentVariables) {
      assumeTrue(new Podman().exists());
      environmentVariables.set("ILO_SHELL_RUNTIME", "podman");
      assertEquals("podman", selectShellRuntime(null).name());
    }

    @Test
    @DisplayName("can force to use nerdctl")
    void forceNerdctl() {
      assumeTrue(new Nerdctl().exists());
      assertEquals("nerdctl", selectShellRuntime(NERDCTL).name());
    }

    @Test
    @DisplayName("can force to use nerdctl with environment variable")
    void forceNerdctlWithEnvVariable(final EnvironmentVariables environmentVariables) {
      assumeTrue(new Nerdctl().exists());
      environmentVariables.set("ILO_SHELL_RUNTIME", "nerdctl");
      assertEquals("nerdctl", selectShellRuntime(null).name());
    }

    @Test
    @DisplayName("can force to use docker")
    void forceDocker() {
      assumeTrue(new Docker().exists());
      assertEquals("docker", selectShellRuntime(DOCKER).name());
    }

    @Test
    @DisplayName("can force to use docker with environment variable")
    void forceDockerWithEnvVariable(final EnvironmentVariables environmentVariables) {
      assumeTrue(new Docker().exists());
      environmentVariables.set("ILO_SHELL_RUNTIME", "docker");
      assertEquals("docker", selectShellRuntime(null).name());
    }

    @Test
    @DisplayName("throws in case there are no shell runtimes available")
    void throwsWithoutAnyRuntime() {
      assumeFalse(new Podman().exists());
      assumeFalse(new Docker().exists());
      assumeFalse(new Nerdctl().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectShellRuntime(null));
    }

    @Test
    @DisplayName("throws in case in case podman is not installed but specified")
    void throwsPodman() {
      assumeFalse(new Podman().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectShellRuntime(PODMAN));
    }

    @Test
    @DisplayName("throws in case in case podman is not installed but specified with an environment variable")
    void throwsPodmanWithEnvVariable(final EnvironmentVariables environmentVariables) {
      assumeFalse(new Podman().exists());
      environmentVariables.set("ILO_SHELL_RUNTIME", "podman");
      assertThrows(NoMatchingRuntimeException.class, () -> selectShellRuntime(null));
    }

    @Test
    @DisplayName("throws in case in case nerdctl is not installed but specified")
    void throwsNerdctl() {
      assumeFalse(new Nerdctl().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectShellRuntime(NERDCTL));
    }

    @Test
    @DisplayName("throws in case in case nerdctl is not installed but specified with an environment variable")
    void throwsNerdctlWithEnvVariable(final EnvironmentVariables environmentVariables) {
      assumeFalse(new Nerdctl().exists());
      environmentVariables.set("ILO_SHELL_RUNTIME", "nerdctl");
      assertThrows(NoMatchingRuntimeException.class, () -> selectShellRuntime(null));
    }

    @Test
    @DisplayName("throws in case in case docker is not installed but specified")
    void throwsDocker() {
      assumeFalse(new Docker().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectShellRuntime(DOCKER));
    }

    @Test
    @DisplayName("throws in case in case docker is not installed but specified with an environment variable")
    void throwsDockerWithEnvVariable(final EnvironmentVariables environmentVariables) {
      assumeFalse(new Docker().exists());
      environmentVariables.set("ILO_SHELL_RUNTIME", "docker");
      assertThrows(NoMatchingRuntimeException.class, () -> selectShellRuntime(null));
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
    @DisplayName("docker is the third choice")
    void dockerCompose2() {
      assumeFalse(new DockerCompose().exists());
      assumeFalse(new PodmanCompose().exists());
      assumeTrue(new DockerCompose2().exists());
      assertEquals("docker", selectComposeRuntime(null).name());
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
    @DisplayName("can force to use docker")
    void forceDockerCompose2() {
      assumeTrue(new DockerCompose2().exists());
      assertEquals("docker", selectComposeRuntime(ComposeRuntime.DOCKER).name());
    }

    @Test
    @DisplayName("throws in case there are no compose runtimes available")
    void throwsWithoutAnyRuntime() {
      assumeFalse(new DockerCompose().exists());
      assumeFalse(new PodmanCompose().exists());
      assumeFalse(new DockerCompose2().exists());
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
    @DisplayName("throws in case in case docker is not installed but specified")
    void throwsDockerCompose2() {
      assumeFalse(new DockerCompose2().exists());
      assertThrows(NoMatchingRuntimeException.class, () -> selectComposeRuntime(ComposeRuntime.DOCKER));
    }

  }

}
