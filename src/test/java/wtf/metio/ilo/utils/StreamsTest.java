/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.test.ClassTests;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Streams")
class StreamsTest {

  @Test
  void filterNonStrings() {
    assertEquals(1, Streams.filter(Stream.of("first", "", null)).count());
  }

  @Test
  void listToStream() {
    assertEquals(2, Streams.fromList(List.of("first", "")).count());
  }

  @Test
  void nullListToStream() {
    assertEquals(0, Streams.fromList(null).count());
  }

  @Test
  void flattenStreams() {
    assertEquals(2, Streams.flatten(Stream.of("first"), Stream.of("second")).size());
  }

  @Test
  void maybe() {
    assertEquals(2, Streams.maybe(true, "first", "second").count());
  }

  @Test
  void maybeNot() {
    assertEquals(0, Streams.maybe(false, "first", "second").count());
  }

  @Test
  @DisplayName("has private constructor")
  void shouldHavePrivateConstructor() throws NoSuchMethodException {
    ClassTests.hasPrivateConstructor(Streams.class);
  }

}