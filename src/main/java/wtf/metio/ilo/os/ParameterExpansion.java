/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.os;

import java.util.function.Function;
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
      matcher.appendReplacement(builder, replacer.apply(matcher.group(MATCHER_GROUP_NAME)));
    }
    matcher.appendTail(builder);
    return builder.toString();
  }

}
