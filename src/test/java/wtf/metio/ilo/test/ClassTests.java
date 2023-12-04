/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.test;

import java.lang.reflect.Modifier;

import static java.lang.reflect.Modifier.isPublic;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ClassTests {

  private ClassTests() {
    // helper class
  }

  public static void hasDefaultConstructor(final Class<?> clazz) throws NoSuchMethodException {
    final var constructor = clazz.getDeclaredConstructor();
    assertNotNull(constructor);
    assertTrue(constructor.trySetAccessible());
    assertTrue(constructor.canAccess(null));
    assertTrue(isPublic(constructor.getModifiers()));
  }

  public static void hasPrivateConstructor(final Class<?> clazz) throws NoSuchMethodException {
    final var constructor = clazz.getDeclaredConstructor();
    assertNotNull(constructor);
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

}
