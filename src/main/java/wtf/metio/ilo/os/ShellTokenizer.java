/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.os;

import java.util.ArrayList;
import java.util.List;

public final class ShellTokenizer {

  private static final int NO_TOKEN_STATE = 0;
  private static final int NORMAL_TOKEN_STATE = 1;
  private static final int SINGLE_QUOTE_STATE = 2;
  private static final int DOUBLE_QUOTE_STATE = 3;

  private ShellTokenizer() {
    // utility class
  }

  /**
   * Tokenizes the given String
   *
   * @param arguments A String containing one or more command-line style arguments to be tokenized.
   * @return A list of parsed and properly escaped arguments.
   */
  public static List<String> tokenize(final String arguments) {
    return tokenize(arguments, false);
  }

  /**
   * Tokenizes the given String into String tokens.
   *
   * @param arguments A String containing one or more command-line style arguments to be tokenized.
   * @param stringify Whether to include escape special characters
   * @return A list of parsed and properly escaped arguments.
   */
  public static List<String> tokenize(final String arguments, final boolean stringify) {
    final var tokens = new ArrayList<String>();
    var builder = new StringBuilder();
    var escaped = false;
    var state = NO_TOKEN_STATE;

    for (var index = 0; index < arguments.length(); index++) {
      final var character = arguments.charAt(index);
      if (escaped) {
        escaped = false;
        builder.append(character);
      } else {
        switch (state) {
          case SINGLE_QUOTE_STATE -> {
            if ('\'' == character) {
              // Seen the close quote; continue this arg until whitespace is seen
              state = NORMAL_TOKEN_STATE;
            } else {
              builder.append(character);
            }
          }
          case DOUBLE_QUOTE_STATE -> {
            if ('"' == character) {
              // Seen the close quote; continue this arg until whitespace is seen
              state = NORMAL_TOKEN_STATE;
            } else if ('\\' == character) {
              // Look ahead, and only escape quotes or backslashes
              index++;
              final var next = arguments.charAt(index);
              if ('"' == next || '\\' == next) {
                builder.append(next);
              } else {
                builder.append(character);
                builder.append(next);
              }
            } else {
              builder.append(character);
            }
          }
          case NO_TOKEN_STATE, NORMAL_TOKEN_STATE -> {
            switch (character) {
              case '\\' -> {
                escaped = true;
                state = NORMAL_TOKEN_STATE;
              }
              case '\'' -> state = SINGLE_QUOTE_STATE;
              case '"' -> state = DOUBLE_QUOTE_STATE;
              default -> {
                if (!Character.isWhitespace(character)) {
                  builder.append(character);
                  state = NORMAL_TOKEN_STATE;
                } else if (NORMAL_TOKEN_STATE == state) {
                  // Whitespace ends the token; start a new one
                  tokens.add(builder.toString());
                  builder = new StringBuilder();
                  state = NO_TOKEN_STATE;
                }
              }
            }
          }
          default -> throw new IllegalStateException("ShellTokenizer state " + state + " is invalid!");
        }
      }
    }

    // If we're still escaped, put in the backslash
    if (escaped) {
      builder.append('\\');
      tokens.add(builder.toString());
    }
    // Close the last argument if we haven't yet
    else if (NO_TOKEN_STATE != state) {
      tokens.add(builder.toString());
    }
    // Format each argument if we've been told to stringify them
    if (stringify) {
      tokens.replaceAll(original -> "\"" + escapeQuotesAndBackslashes(original) + "\"");
    }
    return tokens;
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
