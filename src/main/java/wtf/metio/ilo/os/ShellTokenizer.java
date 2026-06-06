/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import java.util.ArrayList;
import java.util.List;

public final class ShellTokenizer {

  private ShellTokenizer() {
    // utility class
  }

  /**
   * Tokenizes the given String into its argument tokens (no quoting applied).
   *
   * @param arguments A String containing one or more command-line style arguments to be tokenized.
   * @return The argument tokens, with quotes and escapes interpreted and removed.
   */
  public static List<String> tokenize(final String arguments) {
    return tokenize(arguments, false);
  }

  /**
   * Tokenizes the given String into its argument tokens.
   *
   * @param arguments A String containing one or more command-line style arguments to be tokenized.
   * @param stringify When {@code true}, each token is wrapped in double quotes with {@code "} and
   *                  {@code \} escaped and control characters rendered as escape sequences — i.e.
   *                  Java/JSON-style string-literal quoting for display, <em>not</em> shell-safe
   *                  quoting ({@code $} and {@code `} are left as-is, so the result must not be handed
   *                  back to a shell for evaluation).
   * @return The argument tokens, optionally string-literal quoted.
   */
  public static List<String> tokenize(final String arguments, final boolean stringify) {
    final var tokens = new Scanner(arguments).scan();
    if (stringify) {
      tokens.replaceAll(original -> "\"" + escapeQuotesAndBackslashes(original) + "\"");
    }
    return tokens;
  }

  /** The part of the input the scanner is currently inside. */
  private enum State {
    /** Between tokens — only whitespace has been seen since the last token. */
    NO_TOKEN,
    /** Inside an unquoted token. */
    NORMAL_TOKEN,
    /** Inside a single-quoted run, where every character is literal. */
    SINGLE_QUOTE,
    /** Inside a double-quoted run, where a backslash escapes a quote or backslash. */
    DOUBLE_QUOTE
  }

  /**
   * Holds the mutable state of a single tokenization pass and walks the input one character at a
   * time. Each state has its own small handler, so no single method carries the whole machine.
   */
  private static final class Scanner {

    private final String input;
    private final List<String> tokens = new ArrayList<>();
    private StringBuilder builder = new StringBuilder();
    private State state = State.NO_TOKEN;
    private boolean escaped;
    private int index;

    private Scanner(final String input) {
      this.input = input;
    }

    private List<String> scan() {
      for (index = 0; index < input.length(); index++) {
        consume(input.charAt(index));
      }
      finish();
      return tokens;
    }

    private void consume(final char character) {
      if (escaped) {
        escaped = false;
        builder.append(character);
        return;
      }
      switch (state) {
        case SINGLE_QUOTE -> inSingleQuote(character);
        case DOUBLE_QUOTE -> inDoubleQuote(character);
        case NO_TOKEN, NORMAL_TOKEN -> outsideQuotes(character);
      }
    }

    private void inSingleQuote(final char character) {
      if ('\'' == character) {
        // Seen the close quote; continue this arg until whitespace is seen.
        state = State.NORMAL_TOKEN;
      } else {
        builder.append(character);
      }
    }

    private void inDoubleQuote(final char character) {
      if ('"' == character) {
        // Seen the close quote; continue this arg until whitespace is seen.
        state = State.NORMAL_TOKEN;
      } else if ('\\' == character) {
        appendDoubleQuoteEscape();
      } else {
        builder.append(character);
      }
    }

    // Inside double quotes a backslash escapes only a quote or another backslash; any other pair is
    // kept verbatim. A trailing backslash in an unterminated quote has nothing to escape, so it is
    // kept literally. Consuming the escaped character advances the scan position.
    private void appendDoubleQuoteEscape() {
      if (index + 1 >= input.length()) {
        builder.append('\\');
        return;
      }
      final var next = input.charAt(++index);
      if ('"' == next || '\\' == next) {
        builder.append(next);
      } else {
        builder.append('\\').append(next);
      }
    }

    private void outsideQuotes(final char character) {
      switch (character) {
        case '\\' -> {
          escaped = true;
          state = State.NORMAL_TOKEN;
        }
        case '\'' -> state = State.SINGLE_QUOTE;
        case '"' -> state = State.DOUBLE_QUOTE;
        default -> appendOrEndToken(character);
      }
    }

    private void appendOrEndToken(final char character) {
      if (!Character.isWhitespace(character)) {
        builder.append(character);
        state = State.NORMAL_TOKEN;
      } else if (State.NORMAL_TOKEN == state) {
        // Whitespace ends the token; start a new one.
        endToken();
      }
    }

    private void endToken() {
      tokens.add(builder.toString());
      builder = new StringBuilder();
      state = State.NO_TOKEN;
    }

    private void finish() {
      if (escaped) {
        // A trailing, unconsumed backslash is kept literally.
        builder.append('\\');
        tokens.add(builder.toString());
      } else if (State.NO_TOKEN != state) {
        // Close the last argument if we haven't yet.
        tokens.add(builder.toString());
      }
    }
  }

  /**
   * Inserts backslashes before any occurrences of a backslash or
   * quote in the given string.  Also converts any special characters
   * appropriately.
   */
  private static String escapeQuotesAndBackslashes(final String original) {
    final var builder = new StringBuilder(original);

    // Walk backwards, looking for quotes or backslashes.
    //  If we see any, insert an extra backslash into the buffer at
    //  the same index. (By walking backwards, the index into the buffer
    //  will remain correct as we change the buffer.)
    for (var index = original.length() - 1; 0 <= index; index--) {
      final var character = original.charAt(index);
      if ('\\' == character || '"' == character) {
        builder.insert(index, '\\');
      }
      // Replace any special characters with escaped versions
      else if ('\n' == character) {
        builder.deleteCharAt(index);
        builder.insert(index, "\\n");
      } else if ('\t' == character) {
        builder.deleteCharAt(index);
        builder.insert(index, "\\t");
      } else if ('\r' == character) {
        builder.deleteCharAt(index);
        builder.insert(index, "\\r");
      } else if ('\b' == character) {
        builder.deleteCharAt(index);
        builder.insert(index, "\\b");
      } else if ('\f' == character) {
        builder.deleteCharAt(index);
        builder.insert(index, "\\f");
      }
    }
    return builder.toString();
  }

}
