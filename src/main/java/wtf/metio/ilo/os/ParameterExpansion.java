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

  /**
   * Replaces every balanced {@code $(...)} — including nested ones — with the replacer's output. A
   * regular expression cannot match balanced parentheses, so the scan tracks nesting depth; the inner
   * text (which may itself contain {@code $(...)}) is handed to the replacer, where the shell evaluates
   * any nesting. An unbalanced {@code $(} with no closing {@code )} is left untouched rather than
   * corrupted.
   *
   * @param value    The value to scan.
   * @param replacer Produces the replacement for each command's inner text.
   * @return The value with every balanced {@code $(...)} replaced.
   */
  // visible for testing
  final String substituteBalanced(final String value, final Function<? super String, String> replacer) {
    final var result = new StringBuilder();
    var index = 0;
    while (index < value.length()) {
      final var start = value.indexOf("$(", index);
      if (start < 0) {
        result.append(value, index, value.length());
        break;
      }
      result.append(value, index, start);
      final var close = matchingParenthesis(value, start + 2);
      if (close < 0) {
        result.append(value, start, value.length());
        break;
      }
      result.append(replacer.apply(value.substring(start + 2, close)));
      index = close + 1;
    }
    return result.toString();
  }

  // The index of the ')' that closes the '$(' whose body starts at 'from', or -1 when unbalanced.
  private static int matchingParenthesis(final String value, final int from) {
    var depth = 1;
    for (var index = from; index < value.length(); index++) {
      final var character = value.charAt(index);
      if ('(' == character) {
        depth++;
      } else if (')' == character) {
        depth--;
        if (0 == depth) {
          return index;
        }
      }
    }
    return -1;
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
