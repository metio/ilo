/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.test.ClassTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ShellRuntimes")
class ShellRuntimesTest {

  @Test
  @DisplayName("has private constructor")
  void shouldHavePrivateConstructor() throws NoSuchMethodException {
    ClassTests.hasPrivateConstructor(ShellRuntimes.class);
  }

  @Test
  @DisplayName("supports multiple runtimes")
  void shouldSupportMultipleRuntimes() {
    assertEquals(3, ShellRuntimes.allRuntimes().size());
  }

  @Test
  @DisplayName("podman is the first choice")
  void podman() {
    assertEquals("podman", ShellRuntimes.allRuntimes().get(0).name());
  }

  @Test
  @DisplayName("docker is the second choice")
  void docker() {
    assertEquals("docker", ShellRuntimes.allRuntimes().get(1).name());
  }

  @Test
  @DisplayName("lxd is the third choice")
  void lxd() {
    assertEquals("lxc", ShellRuntimes.allRuntimes().get(2).name());
  }

}
