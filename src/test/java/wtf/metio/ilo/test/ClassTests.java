/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.test;

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
  }

}
