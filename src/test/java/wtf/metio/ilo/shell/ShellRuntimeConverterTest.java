/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ShellRuntimeConverter")
class ShellRuntimeConverterTest {

  @ParameterizedTest
  @DisplayName("converts String to ShellRuntime")
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
  void shouldConvertStringToShellRuntime(final String input) {
    // given
    final var converter = new ShellRuntimeConverter();

    // when
    final var runtime = converter.convert(input);

    // then
    assertNotNull(runtime, input);
  }

}
