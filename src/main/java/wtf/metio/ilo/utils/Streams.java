/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
