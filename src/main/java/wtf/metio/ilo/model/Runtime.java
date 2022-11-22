/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.model;

import wtf.metio.ilo.errors.NoMatchingRuntimeException;

import java.util.Arrays;

public interface Runtime<CLI> {

  static <CLI, RUNTIME extends Runtime<CLI>> RUNTIME firstMatching(final String alias, final RUNTIME[] runtimes) {
    return Arrays.stream(runtimes)
      .filter(runtime -> runtime.matches(alias))
      .findFirst()
      .orElseThrow(NoMatchingRuntimeException::new);
  }

  default boolean matches(final String candidate) {
    return Arrays.stream(aliases()).anyMatch(candidate::equalsIgnoreCase);
  }

  String[] aliases();

  CLI cli();

}
