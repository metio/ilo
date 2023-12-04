/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
