/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import wtf.metio.ilo.errors.NoMatchingRuntimeException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static wtf.metio.ilo.shell.ShellRuntime.*;

@DisplayName("ShellRuntime")
@ExtendWith(SystemStubsExtension.class)
class ShellRuntimeTest {

  @ParameterizedTest
  @DisplayName("defines shell runtime constants")
  @ValueSource(strings = {
      "PODMAN",
      "DOCKER",
      "NERDCTL"
  })
  void shouldHaveRuntime(final String runtime) {
    assertNotNull(ShellRuntime.valueOf(runtime));
  }

  @ParameterizedTest
  @DisplayName("supports aliases")
  @ValueSource(strings = {
      "podman",
      "docker",
      "p",
      "d",
      "DOCKER",
      "PODMAN",
      "dOCkeR",
      "podMAN",
      "NERDCTL",
      "nerdctl",
      "nerdCTL",
      "n"
  })
  void shouldSupportAlias(final String alias) {
    assertNotNull(ShellRuntime.fromAlias(alias));
  }

  @Test
  @DisplayName("supports multiple runtimes")
  void shouldSupportMultipleRuntimes() {
    assertEquals(3, values().length);
  }

  @Test
  @DisplayName("podman is the first choice")
  void podman() {
    assertEquals("podman", ShellRuntime.values()[0].cli().name());
  }

  @Test
  @DisplayName("nerdctl is the second choice")
  void nerdctl() {
    assertEquals("nerdctl", ShellRuntime.values()[1].cli().name());
  }

  @Test
  @DisplayName("docker is the third choice")
  void docker() {
    assertEquals("docker", ShellRuntime.values()[2].cli().name());
  }

  @Test
  @DisplayName("podman is the first choice")
  void autoSelectPodman() {
    assumeTrue(new Podman().exists());
    assertEquals("podman", autoSelect(null).name());
  }

  @Test
  @DisplayName("nerdctl is the second choice")
  void autoSelectNerdctl() {
    assumeFalse(new Podman().exists());
    assumeTrue(new Nerdctl().exists());
    assertEquals("nerdctl", autoSelect(null).name());
  }

  @Test
  @DisplayName("docker is the third choice")
  void autoSelectDocker() {
    assumeFalse(new Podman().exists());
    assumeFalse(new Nerdctl().exists());
    assumeTrue(new Docker().exists());
    assertEquals("docker", autoSelect(null).name());
  }

  @Test
  @DisplayName("can force to use podman")
  void forcePodman() {
    assumeTrue(new Podman().exists());
    assertEquals("podman", autoSelect(PODMAN).name());
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("can force to use podman with environment variable")
  void forcePodmanWithEnvVariable(final EnvironmentVariables environmentVariables) {
    assumeTrue(new Podman().exists());
    environmentVariables.set("ILO_SHELL_RUNTIME", "podman");
    assertEquals("podman", autoSelect(null).name());
  }

  @Test
  @DisplayName("can force to use nerdctl")
  void forceNerdctl() {
    assumeTrue(new Nerdctl().exists());
    assertEquals("nerdctl", autoSelect(NERDCTL).name());
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("can force to use nerdctl with environment variable")
  void forceNerdctlWithEnvVariable(final EnvironmentVariables environmentVariables) {
    assumeTrue(new Nerdctl().exists());
    environmentVariables.set("ILO_SHELL_RUNTIME", "nerdctl");
    assertEquals("nerdctl", autoSelect(null).name());
  }

  @Test
  @DisplayName("can force to use docker")
  void forceDocker() {
    assumeTrue(new Docker().exists());
    assertEquals("docker", autoSelect(DOCKER).name());
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("can force to use docker with environment variable")
  void forceDockerWithEnvVariable(final EnvironmentVariables environmentVariables) {
    assumeTrue(new Docker().exists());
    environmentVariables.set("ILO_SHELL_RUNTIME", "docker");
    assertEquals("docker", autoSelect(null).name());
  }

  @Test
  @DisplayName("throws in case there are no shell runtimes available")
  void throwsWithoutAnyRuntime() {
    assumeFalse(new Podman().exists());
    assumeFalse(new Docker().exists());
    assumeFalse(new Nerdctl().exists());
    assertThrows(NoMatchingRuntimeException.class, () -> autoSelect(null));
  }

  @Test
  @DisplayName("throws in case in case podman is not installed but specified")
  void throwsPodman() {
    assumeFalse(new Podman().exists());
    assertThrows(NoMatchingRuntimeException.class, () -> autoSelect(PODMAN));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("throws in case in case podman is not installed but specified with an environment variable")
  void throwsPodmanWithEnvVariable(final EnvironmentVariables environmentVariables) {
    assumeFalse(new Podman().exists());
    environmentVariables.set("ILO_SHELL_RUNTIME", "podman");
    assertThrows(NoMatchingRuntimeException.class, () -> autoSelect(null));
  }

  @Test
  @DisplayName("throws in case in case nerdctl is not installed but specified")
  void throwsNerdctl() {
    assumeFalse(new Nerdctl().exists());
    assertThrows(NoMatchingRuntimeException.class, () -> autoSelect(NERDCTL));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("throws in case in case nerdctl is not installed but specified with an environment variable")
  void throwsNerdctlWithEnvVariable(final EnvironmentVariables environmentVariables) {
    assumeFalse(new Nerdctl().exists());
    environmentVariables.set("ILO_SHELL_RUNTIME", "nerdctl");
    assertThrows(NoMatchingRuntimeException.class, () -> autoSelect(null));
  }

  @Test
  @DisplayName("throws in case in case docker is not installed but specified")
  void throwsDocker() {
    assumeFalse(new Docker().exists());
    assertThrows(NoMatchingRuntimeException.class, () -> autoSelect(DOCKER));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("throws in case in case docker is not installed but specified with an environment variable")
  void throwsDockerWithEnvVariable(final EnvironmentVariables environmentVariables) {
    assumeFalse(new Docker().exists());
    environmentVariables.set("ILO_SHELL_RUNTIME", "docker");
    assertThrows(NoMatchingRuntimeException.class, () -> autoSelect(null));
  }

}
