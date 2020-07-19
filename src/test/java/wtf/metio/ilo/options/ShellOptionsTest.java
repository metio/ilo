/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.options;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wtf.metio.ilo.test.ClassTests;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ShellOptions")
class ShellOptionsTest {

  @Test
  @DisplayName("has default constructor")
  void shouldHaveDefaultConstructor() throws NoSuchMethodException {
    ClassTests.hasDefaultConstructor(ShellOptions.class);
  }

  @ParameterizedTest
  @DisplayName("has public fields")
  @ValueSource(strings = {
      "runtime",
      "image",
      "hostname",
      "name",
      "debug",
      "interactive",
      "mountProjectDir",
      "commands"
  })
  void shouldHavePublicProperty(final String field) throws NoSuchFieldException {
    final var runtime = ShellOptions.class.getDeclaredField(field);
    assertTrue(Modifier.isPublic(runtime.getModifiers()));
  }

}
