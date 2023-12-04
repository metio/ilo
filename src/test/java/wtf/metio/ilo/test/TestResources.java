/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
