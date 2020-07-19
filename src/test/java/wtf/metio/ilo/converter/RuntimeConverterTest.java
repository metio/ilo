/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("RuntimeConverter")
class RuntimeConverterTest {

  @ParameterizedTest
  @DisplayName("converts String to Runtime")
  @ValueSource(strings = {
      "podman",
      "docker",
      "p",
      "d",
      "DOCKER",
      "PODMAN",
      "dOCkeR",
      "podMAN"
  })
  void shouldConvertStringToComposeRuntime(final String input) {
    // given
    final var converter = new RuntimeConverter();

    // when
    final var runtime = converter.convert(input);

    // then
    assertNotNull(runtime, () -> input);
  }

}
