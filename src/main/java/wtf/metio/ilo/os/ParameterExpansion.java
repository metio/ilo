/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.os;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class ParameterExpansion {

  // visible for testing
  static final String MATCHER_GROUP_NAME = "expression";

  abstract String substituteCommands(String value);

  abstract String expandParameters(String value);

  /**
   * Expands a leading {@code ~} to the current user's home directory. Only a {@code ~} that begins a
   * path component — at the start of the value or right after a {@code :} or {@code =} separator —
   * and that stands alone — followed by {@code /}, {@code :} or the end of the value — denotes the
   * home directory. Any other {@code ~} (inside a path, or introducing another user's name like
   * {@code ~bob}) is left untouched so it is not corrupted.
   *
   * @param value The value to expand.
   * @return The value with a qualifying leading tilde replaced by the home directory.
   */
  // visible for testing
  final String expandTilde(final String value) {
    final var userHome = System.getProperty("user.home");
    return value.replaceAll("(^|[:=])~(?=[/:]|$)", "$1" + Matcher.quoteReplacement(userHome));
  }

  // visible for testing
  final String replace(final String value, final Function<? super String, String> replacer, final Pattern... patterns) {
    var current = value;
    for (final var pattern : patterns) {
      current = replace(current, replacer, pattern);
    }
    return current;
  }

  private String replace(final String value, final Function<? super String, String> replacer, final Pattern pattern) {
    final var builder = new StringBuilder();
    final var matcher = pattern.matcher(value);
    while (matcher.find()) {
      // The replacer returns a literal expansion (env value or command output). Quote it so that
      // any '$' or '\' it contains is not interpreted by appendReplacement as a group reference.
      final var replacement = replacer.apply(matcher.group(MATCHER_GROUP_NAME));
      matcher.appendReplacement(builder, Matcher.quoteReplacement(replacement));
    }
    matcher.appendTail(builder);
    return builder.toString();
  }

}
