/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.model;

import wtf.metio.ilo.errors.NoMatchingRuntimeException;

import java.util.Arrays;
import java.util.Optional;

public interface Runtime<CLI> {

  static <CLI, RUNTIME extends Runtime<CLI>> RUNTIME firstMatching(final String alias, final RUNTIME[] runtimes) {
    return Arrays.stream(runtimes)
        .filter(runtime -> runtime.matches(alias))
        .findFirst()
        .orElseThrow(NoMatchingRuntimeException::new);
  }

  /**
   * Reads a runtime preference from an environment variable. An unset or blank variable yields no
   * preference; an unrecognized value is reported and also yields none, so the caller falls back to
   * auto-detection rather than aborting on a typo.
   *
   * @param variableName The environment variable to read.
   * @param runtimes     The candidate runtimes to match against.
   * @return The matching runtime, or empty when the variable is unset, blank, or unrecognized.
   */
  static <CLI, RUNTIME extends Runtime<CLI>> Optional<RUNTIME> fromEnvironment(
      final String variableName, final RUNTIME[] runtimes) {
    final var value = System.getenv(variableName);
    if (null == value || value.isBlank()) {
      return Optional.empty();
    }
    final var match = Arrays.stream(runtimes).filter(runtime -> runtime.matches(value)).findFirst();
    if (match.isEmpty()) {
      System.err.println("Ignoring unknown " + variableName + " value '" + value
          + "'; falling back to runtime auto-detection.");
    }
    return match;
  }

  default boolean matches(final String candidate) {
    return Arrays.stream(aliases()).anyMatch(candidate::equalsIgnoreCase);
  }

  String[] aliases();

  CLI cli();

}
