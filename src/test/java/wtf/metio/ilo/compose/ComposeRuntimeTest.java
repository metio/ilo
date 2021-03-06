/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ComposeRuntime")
class ComposeRuntimeTest {

  @ParameterizedTest
  @DisplayName("defines compose runtime constants")
  @ValueSource(strings = {
    "PODMAN_COMPOSE",
    "PODS_COMPOSE",
    "DOCKER_COMPOSE"
  })
  void shouldHaveRuntime(final String runtime) {
    assertNotNull(ComposeRuntime.valueOf(runtime));
  }

  @ParameterizedTest
  @DisplayName("supports aliases")
  @ValueSource(strings = {
    "podman-compose",
    "pods-compose",
    "docker-compose",
    "pc",
    "dc",
    "pods",
    "DOCKER-COMPOSE",
    "PODMAN-COMPOSE",
    "dOCkeR-cOMpOSe",
    "pODs-cOMpOSe",
    "podMAN-compOSe"
  })
  void shouldSupportAlias(final String alias) {
    assertNotNull(ComposeRuntime.fromAlias(alias));
  }

}
