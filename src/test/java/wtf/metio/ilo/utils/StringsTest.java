/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wtf.metio.ilo.test.ClassTests;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
  void shouldDetectBlankStringWithValues(final String value) {
    assertFalse(Strings.isBlank(value));
  }

  @DisplayName("isBlank")
  @ParameterizedTest
  @ValueSource(strings = {"a", " b ", "  c  "})
  void shouldDetectNonBlankString(final String value) {
    assertTrue(Strings.isNotBlank(value));
  }

  @DisplayName("isBlank")
  @ParameterizedTest
  @ValueSource(strings = {"", " ", "  "})
  void shouldDetectNonBlankStringWithoutValues(final String value) {
    assertFalse(Strings.isNotBlank(value));
  }

  @Test
  @DisplayName("has private constructor")
  void shouldHavePrivateConstructor() throws NoSuchMethodException {
    ClassTests.hasPrivateConstructor(Strings.class);
  }

}
