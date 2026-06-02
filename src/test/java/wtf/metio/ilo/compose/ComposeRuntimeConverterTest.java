/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.compose;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ComposeRuntimeConverter")
class ComposeRuntimeConverterTest {

  @ParameterizedTest
  @DisplayName("converts String to ComposeRuntime")
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
  void shouldConvertStringToComposeRuntime(final String input) {
    // given
    final var converter = new ComposeRuntimeConverter();

    // when
    final var runtime = converter.convert(input);

    // then
    assertNotNull(runtime, () -> input);
  }

}
