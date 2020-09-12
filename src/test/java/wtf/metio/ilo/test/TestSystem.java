/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.test;

import org.junit.jupiter.api.function.Executable;

public final class TestSystem {

  public static void withProperty(final String name, final String value, final Executable executable) {
    final var currentValue = System.getProperty(name);
    System.setProperty(name, value);
    try {
      executable.execute();
    } catch (final Throwable throwable) {
      throw new RuntimeException(throwable);
    } finally {
      System.setProperty(name, currentValue);
    }
  }
  
  private TestSystem() {
    // utility class
  }

}
