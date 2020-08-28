/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.model;

import java.util.Arrays;

public interface Runtime {

  static <RUNTIME extends Runtime> RUNTIME firstMatching(final String alias, final RUNTIME[] matchers) {
    return Arrays.stream(matchers)
        .filter(runtime -> runtime.matches(alias))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

  default boolean matches(final String candidate) {
    return Arrays.stream(aliases()).anyMatch(candidate::equalsIgnoreCase);
  }

  String[] aliases();

}
