/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import java.util.regex.Matcher;

/**
 * Expansion for a real host shell. A single left-to-right scan over the original value substitutes
 * each command ({@code $(...)} and, where the shell supports it, {@code `...`}) and each parameter
 * ({@code $NAME} / {@code ${NAME}}) in place. The output of a substitution is emitted verbatim and is
 * never re-scanned, matching shell semantics: a parameter's value is not re-evaluated for command
 * substitution, and a command's output is not re-evaluated for further expansion. An unbalanced
 * {@code $(} or {@code `}, and a {@code $} that does not introduce a name, are left untouched rather
 * than corrupted.
 *
 * <p>Two intentional limitations: there is no escape mechanism, so every {@code $}, backtick and
 * leading {@code ~} is expanded and a value cannot carry one literally (a {@code \} is not treated as
 * an escape — it stays part of the value, so Windows paths like {@code C:\Users} are preserved). And
 * only a bare {@code ${NAME}} is expanded; a brace expression with a modifier such as
 * {@code ${VAR:-default}} is passed through verbatim rather than evaluated.</p>
 */
abstract class ShellExpansion extends ParameterExpansion {

  @Override
  final String expand(final String value) {
    return scan(expandTilde(value));
  }

  // Runs a command-substitution body through the shell and returns its output.
  abstract String commandOutput(String script);

  // Expands a parameter reference (including its leading '$') to the variable's value.
  abstract String parameterValue(String reference);

  // POSIX shells treat `...` as command substitution; PowerShell uses the backtick as an escape
  // character, so it overrides this to false.
  boolean backticksAreCommands() {
    return true;
  }

  // Extra characters (besides '/' and ':') that may follow a leading '~' and still denote the home
  // directory, as a regex character-class fragment. Empty for POSIX, where '\' is an ordinary filename
  // character; PowerShell adds the Windows path separator so '~\work' expands.
  String tildeFollowers() {
    return "";
  }

  /**
   * Expands a leading {@code ~} to the current user's home directory. Only a {@code ~} that begins a
   * path component — at the start of the value or right after a {@code :} or {@code =} separator —
   * and that stands alone — followed by {@code /}, {@code :} or the end of the value — denotes the
   * home directory. Any other {@code ~} (inside a path, or introducing another user's name like
   * {@code ~bob}) is left untouched so it is not corrupted.
   *
   * @param value The value to expand.
   * @return The value with a qualifying leading tilde replaced by the home directory.
   */
  // visible for testing
  final String expandTilde(final String value) {
    final var userHome = System.getProperty("user.home");
    return value.replaceAll("(^|[:=])~(?=[/:" + tildeFollowers() + "]|$)", "$1" + Matcher.quoteReplacement(userHome));
  }

  // The single expansion pass. Each branch consumes one construct from the ORIGINAL value and appends
  // its replacement; nothing already appended is ever revisited, so a substitution's output cannot
  // trigger a further substitution.
  private String scan(final String value) {
    final var result = new StringBuilder();
    final var length = value.length();
    var index = 0;
    while (index < length) {
      final var character = value.charAt(index);
      if ('$' == character && index + 1 < length && '(' == value.charAt(index + 1)) {
        final var close = matchingParenthesis(value, index + 2);
        if (close < 0) {
          result.append(value, index, length);
          break;
        }
        result.append(commandOutput(value.substring(index + 2, close)));
        index = close + 1;
      } else if ('$' == character && index + 1 < length && '{' == value.charAt(index + 1)) {
        final var end = value.indexOf('}', index + 2);
        if (end < 0) {
          // no closing brace: leave the '$' literal and keep scanning after it
          result.append(character);
          index++;
        } else if (isParameterName(value, index + 2, end)) {
          result.append(parameterValue("$" + value.substring(index + 2, end)));
          index = end + 1;
        } else {
          // a brace expression ilo does not expand (e.g. '${VAR:-default}'): emit it literally as a
          // whole, so an embedded '$(...)' inside it is not mistaken for a command substitution
          result.append(value, index, end + 1);
          index = end + 1;
        }
      } else if ('$' == character && index + 1 < length && isNameStart(value.charAt(index + 1))) {
        var end = index + 1;
        while (end < length && isNameChar(value.charAt(end))) {
          end++;
        }
        result.append(parameterValue(value.substring(index, end)));
        index = end;
      } else if ('`' == character && backticksAreCommands()) {
        final var close = value.indexOf('`', index + 1);
        if (close < 0) {
          result.append(value, index, length);
          break;
        }
        result.append(commandOutput(value.substring(index + 1, close)));
        index = close + 1;
      } else {
        result.append(character);
        index++;
      }
    }
    return result.toString();
  }

  // The index of the ')' that closes the '$(' whose body starts at 'from', tracking nesting depth so a
  // nested '$(...)' is kept within the outer body. -1 when there is no matching ')'.
  private static int matchingParenthesis(final String value, final int from) {
    var depth = 1;
    for (var index = from; index < value.length(); index++) {
      final var character = value.charAt(index);
      if ('(' == character) {
        depth++;
      } else if (')' == character) {
        depth--;
        if (0 == depth) {
          return index;
        }
      }
    }
    return -1;
  }

  // A parameter name is a letter followed by letters, digits or underscores, matching the references a
  // shell expands as '$NAME'/'${NAME}'.
  private static boolean isParameterName(final String value, final int start, final int end) {
    if (start >= end || !isNameStart(value.charAt(start))) {
      return false;
    }
    for (var index = start + 1; index < end; index++) {
      if (!isNameChar(value.charAt(index))) {
        return false;
      }
    }
    return true;
  }

  private static boolean isNameStart(final char character) {
    return (character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z');
  }

  private static boolean isNameChar(final char character) {
    return isNameStart(character) || (character >= '0' && character <= '9') || '_' == character;
  }

}
