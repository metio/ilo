/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.test;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestResources {

  public static Path testResources() {
    return Paths.get("src/test/resources/");
  }

  public static Path testResources(final Class<?> clazz) {
    return testResources().resolve(clazz.getName().replace(".", "/"));
  }

  private TestResources() {
    // utility class
  }

}
