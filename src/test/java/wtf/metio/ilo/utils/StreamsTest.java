/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
  void optional() {
    assertEquals(2, Streams.optional("--option", "value").count());
  }

  @Test
  void optionalNot() {
    assertEquals(0, Streams.optional("--option", null).count());
  }

  @Test
  void optionalEmpty() {
    assertEquals(0, Streams.optional("--option", "").count());
  }

  @Test
  @DisplayName("has private constructor")
  void shouldHavePrivateConstructor() throws NoSuchMethodException {
    ClassTests.hasPrivateConstructor(Streams.class);
  }

}
