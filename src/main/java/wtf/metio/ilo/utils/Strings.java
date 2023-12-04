/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.utils;

import java.util.Objects;

public final class Strings {

  public static boolean isBlank(final String value) {
    return Objects.isNull(value) || value.isBlank();
  }

  public static boolean isNotBlank(final String value) {
    return !isBlank(value);
  }

  private Strings() {
    // utility class
  }

}
