/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.devcontainer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wtf.metio.ilo.test.ClassTests;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DevcontainerOptions")
class DevcontainerOptionsTest {

  @Test
  @DisplayName("has default constructor")
  void shouldHaveDefaultConstructor() throws NoSuchMethodException {
    ClassTests.hasDefaultConstructor(DevcontainerOptions.class);
  }

  @ParameterizedTest
  @DisplayName("has public fields")
  @ValueSource(strings = {
      "shellRuntime",
      "composeRuntime",
      "debug",
      "mountProjectDir",
      "pull",
      "removeImage",
      "executeInitializeCommand",
      "executeOnCreateCommand",
      "executeUpdateContentCommand",
      "executePostCreateCommand",
      "executePostStartCommand",
      "executePostAttachCommand",
      "locations"
  })
  void shouldHavePublicProperty(final String field) throws NoSuchFieldException {
    final var runtime = DevcontainerOptions.class.getDeclaredField(field);
    assertTrue(Modifier.isPublic(runtime.getModifiers()));
  }

  @ParameterizedTest
  @DisplayName("returns debug value")
  @ValueSource(booleans = {true, false})
  void shouldReturnDebugValue(final boolean value) {
    final var options = new DevcontainerOptions();
    options.debug = value;
    assertEquals(value, options.debug());
  }

}
