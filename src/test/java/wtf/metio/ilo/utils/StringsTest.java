/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Strings")
class StringsTest {

  @DisplayName("isBlank")
  @ParameterizedTest
  @ValueSource(strings = {"", " ", "  "})
  void shouldDetectBlankString(final String value) {
    assertTrue(Strings.isBlank(value));
  }

  @DisplayName("isBlank")
  @ParameterizedTest
  @ValueSource(strings = {"a", " b ", "  c  "})
  void shouldDetectNonBlankString(final String value) {
    assertTrue(Strings.isNotBlank(value));
  }

}