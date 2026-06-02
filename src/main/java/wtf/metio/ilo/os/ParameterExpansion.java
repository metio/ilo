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
