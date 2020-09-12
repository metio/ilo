/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.tools.Docker;
import wtf.metio.ilo.tools.LXD;
import wtf.metio.ilo.tools.Podman;

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
    assumeTrue(new Podman().exists() || new Docker().exists() || new LXD().exists());
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
  @DisplayName("returns non-null values for forced lxd usage")
  void shouldReturnNonNullValueForLXD() {
    assumeTrue(new LXD().exists());
    assertNotNull(shellExecutor.selectRuntime(ShellRuntime.LXD));
  }

}