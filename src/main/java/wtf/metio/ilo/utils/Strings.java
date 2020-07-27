/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.utils;

public final class Strings {

  public static boolean isBlank(final String value) {
    return null != value && !value.isBlank();
  }

  public static boolean isNotBlank(final String value) {
    return null == value || value.isBlank();
  }

  private Strings() {
    // utility class
  }

}
