/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DisplayName("ShellExecutor")
class ShellExecutorTest {

  private ShellExecutor shellExecutor;

  @BeforeEach
  void setUp() {
    shellExecutor = new ShellExecutor();
  }

  @Test
  @DisplayName("returns non-null values for auto-selection")
  void shouldReturnNonNullValueForAutoSelection() {
    assumeTrue(new Podman().exists() || new Docker().exists() || new Nerdctl().exists());
    assertNotNull(shellExecutor.selectRuntime(null));
  }

  @Test
  @DisplayName("returns non-null values for forced podman usage")
  void shouldReturnNonNullValueForPodman() {
    assumeTrue(new Podman().exists());
    assertNotNull(shellExecutor.selectRuntime(ShellRuntime.PODMAN));
  }

  @Test
  @DisplayName("returns non-null values for forced docker usage")
  void shouldReturnNonNullValueForDocker() {
    assumeTrue(new Docker().exists());
    assertNotNull(shellExecutor.selectRuntime(ShellRuntime.DOCKER));
  }

  @Test
  @DisplayName("returns non-null values for forced nerdctl usage")
  void shouldReturnNonNullValueForNerdctl() {
    assumeTrue(new Nerdctl().exists());
    assertNotNull(shellExecutor.selectRuntime(ShellRuntime.NERDCTL));
  }

}
