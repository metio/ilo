/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ShellTokenizer")
class ShellTokenizerTest {

  @Nested
  @DisplayName("basic tokenization")
  class BasicTokenization {

    @Test
    @DisplayName("an empty string yields no tokens")
    void empty() {
      assertTrue(ShellTokenizer.tokenize("").isEmpty());
    }

    @Test
    @DisplayName("whitespace only yields no tokens")
    void whitespaceOnly() {
      assertTrue(ShellTokenizer.tokenize("   ").isEmpty());
    }

    @Test
    @DisplayName("a single token")
    void singleToken() {
      assertIterableEquals(List.of("echo"), ShellTokenizer.tokenize("echo"));
    }

    @Test
    @DisplayName("several tokens separated by single spaces")
    void severalTokens() {
      assertIterableEquals(List.of("a", "b", "c"), ShellTokenizer.tokenize("a b c"));
    }

    @Test
    @DisplayName("runs of whitespace collapse between tokens")
    void collapsesWhitespace() {
      assertIterableEquals(List.of("a", "b"), ShellTokenizer.tokenize("a    b"));
    }

    @Test
    @DisplayName("leading and trailing whitespace is ignored")
    void trimsSurroundingWhitespace() {
      assertIterableEquals(List.of("a", "b"), ShellTokenizer.tokenize("   a b   "));
    }

    @Test
    @DisplayName("tabs and newlines also separate tokens")
    void splitsOnTabsAndNewlines() {
      assertIterableEquals(List.of("a", "b", "c"), ShellTokenizer.tokenize("a\tb\nc"));
    }

    @Test
    @DisplayName("a realistic command line")
    void realisticCommandLine() {
      assertIterableEquals(
          List.of("mysql", "-u", "root", "-p", "my database"),
          ShellTokenizer.tokenize("mysql -u root -p 'my database'"));
    }
  }

  @Nested
  @DisplayName("single quotes")
  class SingleQuotes {

    @Test
    @DisplayName("keep whitespace verbatim")
    void keepWhitespace() {
      assertIterableEquals(List.of("a b"), ShellTokenizer.tokenize("'a b'"));
    }

    @Test
    @DisplayName("treat a backslash literally")
    void backslashIsLiteral() {
      // input: 'a\b' -> the backslash is not special inside single quotes
      assertIterableEquals(List.of("a\\b"), ShellTokenizer.tokenize("'a\\b'"));
    }

    @Test
    @DisplayName("treat a double quote literally")
    void doubleQuoteIsLiteral() {
      assertIterableEquals(List.of("a\"b"), ShellTokenizer.tokenize("'a\"b'"));
    }

    @Test
    @DisplayName("an empty pair yields one empty token")
    void emptyQuotesYieldEmptyToken() {
      assertIterableEquals(List.of(""), ShellTokenizer.tokenize("''"));
    }

    @Test
    @DisplayName("quoted and unquoted parts join into one token")
    void joinWithSurroundingText() {
      assertIterableEquals(List.of("abc"), ShellTokenizer.tokenize("a'b'c"));
    }

    @Test
    @DisplayName("a value after '=' stays attached")
    void attachedAfterEquals() {
      assertIterableEquals(List.of("echo", "foo=bar"), ShellTokenizer.tokenize("echo foo='bar'"));
    }

    @Test
    @DisplayName("an unterminated quote runs to the end")
    void unterminatedRunsToEnd() {
      assertIterableEquals(List.of("a b"), ShellTokenizer.tokenize("'a b"));
    }
  }

  @Nested
  @DisplayName("double quotes")
  class DoubleQuotes {

    @Test
    @DisplayName("keep whitespace verbatim")
    void keepWhitespace() {
      assertIterableEquals(List.of("a b"), ShellTokenizer.tokenize("\"a b\""));
    }

    @Test
    @DisplayName("an empty pair yields one empty token")
    void emptyQuotesYieldEmptyToken() {
      assertIterableEquals(List.of(""), ShellTokenizer.tokenize("\"\""));
    }

    @Test
    @DisplayName("a backslash escapes a double quote")
    void escapesQuote() {
      // input: "a\"b" -> a"b
      assertIterableEquals(List.of("a\"b"), ShellTokenizer.tokenize("\"a\\\"b\""));
    }

    @Test
    @DisplayName("a backslash escapes a backslash")
    void escapesBackslash() {
      // input: "a\\b" -> a\b
      assertIterableEquals(List.of("a\\b"), ShellTokenizer.tokenize("\"a\\\\b\""));
    }

    @Test
    @DisplayName("a backslash before any other character is kept verbatim")
    void keepsOtherEscape() {
      // input: "a\nb" (backslash + n) -> a\nb (both characters retained)
      assertIterableEquals(List.of("a\\nb"), ShellTokenizer.tokenize("\"a\\nb\""));
    }

    @Test
    @DisplayName("a trailing backslash in an unterminated quote is kept")
    void keepsTrailingBackslash() {
      // input: "\ (open quote then a single backslash at end of input)
      assertIterableEquals(List.of("\\"), ShellTokenizer.tokenize("\"\\"));
    }

    @Test
    @DisplayName("an unterminated quote runs to the end")
    void unterminatedRunsToEnd() {
      assertIterableEquals(List.of("a b"), ShellTokenizer.tokenize("\"a b"));
    }
  }

  @Nested
  @DisplayName("backslash escaping outside quotes")
  class BackslashEscaping {

    @Test
    @DisplayName("an escaped space joins the token")
    void escapedSpaceJoinsToken() {
      // input: a\ b -> "a b" as a single token
      assertIterableEquals(List.of("a b"), ShellTokenizer.tokenize("a\\ b"));
    }

    @Test
    @DisplayName("an escaped double quote becomes a literal quote")
    void escapedQuoteIsLiteral() {
      // input: \" -> "
      assertIterableEquals(List.of("\""), ShellTokenizer.tokenize("\\\""));
    }

    @Test
    @DisplayName("an escaped backslash becomes a single backslash")
    void escapedBackslash() {
      // input: \\ -> \
      assertIterableEquals(List.of("\\"), ShellTokenizer.tokenize("\\\\"));
    }

    @Test
    @DisplayName("a trailing backslash is kept")
    void trailingBackslashKept() {
      assertIterableEquals(List.of("a\\"), ShellTokenizer.tokenize("a\\"));
    }

    @Test
    @DisplayName("a lone trailing backslash becomes a backslash token")
    void loneTrailingBackslash() {
      assertIterableEquals(List.of("\\"), ShellTokenizer.tokenize("\\"));
    }
  }

  @Nested
  @DisplayName("mixed quoting")
  class MixedQuoting {

    @Test
    @DisplayName("double- and single-quoted runs join into one token")
    void adjacentQuotesJoin() {
      // input: "a"b'c' -> abc
      assertIterableEquals(List.of("abc"), ShellTokenizer.tokenize("\"a\"b'c'"));
    }

    @Test
    @DisplayName("a quoted argument among plain ones")
    void quotedAmongPlain() {
      assertIterableEquals(
          List.of("echo", "hello world", "!"), ShellTokenizer.tokenize("echo \"hello world\" !"));
    }

    @Test
    @DisplayName("single quotes inside double quotes are literal")
    void singleInsideDouble() {
      assertIterableEquals(List.of("it's"), ShellTokenizer.tokenize("\"it's\""));
    }

    @Test
    @DisplayName("double quotes inside single quotes are literal")
    void doubleInsideSingle() {
      assertIterableEquals(List.of("say \"hi\""), ShellTokenizer.tokenize("'say \"hi\"'"));
    }
  }

  @Nested
  @DisplayName("stringify")
  class Stringify {

    @Test
    @DisplayName("the default does not stringify")
    void defaultDoesNotStringify() {
      assertIterableEquals(
          ShellTokenizer.tokenize("a b"), ShellTokenizer.tokenize("a b", false));
    }

    @Test
    @DisplayName("wraps each token in double quotes")
    void wrapsTokens() {
      assertIterableEquals(List.of("\"a\"", "\"b\""), ShellTokenizer.tokenize("a b", true));
    }

    @Test
    @DisplayName("wraps an empty token")
    void wrapsEmptyToken() {
      assertIterableEquals(List.of("\"\""), ShellTokenizer.tokenize("''", true));
    }

    @Test
    @DisplayName("escapes embedded quotes and backslashes")
    void escapesQuotesAndBackslashes() {
      // The single quotes pass 'a"b\c' through verbatim, so the token holds a quote and a backslash.
      assertEquals(List.of("\"a\\\"b\\\\c\""), ShellTokenizer.tokenize("'a\"b\\c'", true));
    }

    @Test
    @DisplayName("escapes control characters")
    void escapesControlCharacters() {
      assertEquals(
          List.of("\"a\\nb\\tc\\rd\\be\\ff\""),
          ShellTokenizer.tokenize("'a\nb\tc\rd\be\ff'", true));
    }

    @Test
    @DisplayName("escapes a special character at the very start of a token")
    void escapesLeadingSpecialCharacter() {
      assertEquals(List.of("\"\\\"abc\""), ShellTokenizer.tokenize("'\"abc'", true));
    }
  }
}
