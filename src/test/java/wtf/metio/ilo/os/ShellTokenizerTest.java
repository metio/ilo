/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class ShellTokenizerTest {

  @TestFactory
  Stream<DynamicTest> tokenizeStrings() {
    return Map.ofEntries(
            Map.entry("echo", List.of("echo")),
            Map.entry("mysql -u root -p 'my database'", List.of("mysql", "-u", "root", "-p", "my database")),
            Map.entry("echo foo='bar'", List.of("echo", "foo=bar"))
        )
        .entrySet()
        .stream()
        .map(entry -> DynamicTest.dynamicTest(entry.getKey(), () -> {
          final var tokens = ShellTokenizer.tokenize(entry.getKey());
          assertIterableEquals(entry.getValue(), tokens);
        }));
  }

  @Test
  @DisplayName("keeps whitespace inside single quotes")
  void keepsWhitespaceInSingleQuotes() {
    assertEquals(List.of("a b"), ShellTokenizer.tokenize("'a b'"));
  }

  @Test
  @DisplayName("escapes a quote inside double quotes")
  void escapesQuoteInsideDoubleQuotes() {
    assertEquals(List.of("a\"b"), ShellTokenizer.tokenize("\"a\\\"b\""));
  }

  @Test
  @DisplayName("escapes a backslash inside double quotes")
  void escapesBackslashInsideDoubleQuotes() {
    assertEquals(List.of("a\\b"), ShellTokenizer.tokenize("\"a\\\\b\""));
  }

  @Test
  @DisplayName("keeps a backslash that escapes neither quote nor backslash")
  void keepsOtherEscapeInsideDoubleQuotes() {
    assertEquals(List.of("a\\nb"), ShellTokenizer.tokenize("\"a\\nb\""));
  }

  @Test
  @DisplayName("keeps a trailing backslash inside an unterminated double quote")
  void keepsTrailingBackslashInDoubleQuote() {
    assertEquals(List.of("\\"), ShellTokenizer.tokenize("\"\\"));
  }

  @Test
  @DisplayName("keeps a trailing backslash outside of quotes")
  void keepsTrailingBackslashOutsideQuotes() {
    assertEquals(List.of("a\\"), ShellTokenizer.tokenize("a\\"));
  }

  @Test
  @DisplayName("stringify quotes each token and escapes quotes and backslashes")
  void stringifyEscapesQuotesAndBackslashes() {
    // The single quotes pass 'a"b\c' through verbatim, so the token holds a quote and a backslash.
    final var tokens = ShellTokenizer.tokenize("'a\"b\\c'", true);
    assertEquals(List.of("\"a\\\"b\\\\c\""), tokens);
  }

  @Test
  @DisplayName("stringify escapes control characters")
  void stringifyEscapesControlCharacters() {
    final var tokens = ShellTokenizer.tokenize("'a\nb\tc\rd\be\ff'", true);
    assertEquals(List.of("\"a\\nb\\tc\\rd\\be\\ff\""), tokens);
  }

  @Test
  @DisplayName("stringify escapes a special character at the very start of a token")
  void stringifyEscapesLeadingSpecialCharacter() {
    assertEquals(List.of("\"\\\"abc\""), ShellTokenizer.tokenize("'\"abc'", true));
  }

}
