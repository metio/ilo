/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Stream.of;

public final class Streams {

  public static Stream<String> filter(final Stream<String> stream) {
    return stream
        .filter(Objects::nonNull)
        .filter(not(String::isBlank));
  }

  public static Stream<String> fromList(final List<String> list) {
    return Stream.ofNullable(list).flatMap(Collection::stream);
  }

  @SafeVarargs
  public static List<String> flatten(final Stream<String>... streams) {
    return filter(Arrays.stream(streams).flatMap(identity())).toList();
  }

  public static Stream<String> maybe(final boolean condition, final String... values) {
    return condition ? filter(Arrays.stream(values)) : Stream.empty();
  }

  public static Stream<String> optional(final String prefix, final String value) {
    return Objects.nonNull(value) && !value.isBlank() ? of(prefix, value) : Stream.empty();
  }

  public static Stream<String> withPrefix(final String prefix, final List<String> values) {
    return filter(fromList(values)).flatMap(value -> of(prefix, value));
  }

  private Streams() {
    // utility class
  }

}
