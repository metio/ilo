/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.test.ClassTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ComposeRuntimes")
class ComposeRuntimesTest {

  @Test
  @DisplayName("has private constructor")
  void shouldHavePrivateConstructor() throws NoSuchMethodException {
    ClassTests.hasPrivateConstructor(ComposeRuntimes.class);
  }

  @Test
  @DisplayName("supports multiple runtimes")
  void shouldSupportMultipleRuntimes() {
    assertEquals(6, ComposeRuntimes.allRuntimes().size());
  }

  @Test
  @DisplayName("docker-compose is the first choice")
  void dockerCompose() {
    assertEquals("docker-compose", ComposeRuntimes.allRuntimes().get(0).name());
  }

  @Test
  @DisplayName("podman-compose is the second choice")
  void podmanCompose() {
    assertEquals("podman-compose", ComposeRuntimes.allRuntimes().get(1).name());
  }

  @Test
  @DisplayName("pods-compose is the third choice")
  void podsCompose() {
    assertEquals("pods-compose", ComposeRuntimes.allRuntimes().get(2).name());
  }

  @Test
  @DisplayName("vagrant is the fourth choice")
  void vagrant() {
    assertEquals("vagrant", ComposeRuntimes.allRuntimes().get(3).name());
  }

  @Test
  @DisplayName("footloose is the fifth choice")
  void footloose() {
    assertEquals("footloose", ComposeRuntimes.allRuntimes().get(4).name());
  }

  @Test
  @DisplayName("vagga is the sixth choice")
  void vagga() {
    assertEquals("vagga", ComposeRuntimes.allRuntimes().get(5).name());
  }

}
