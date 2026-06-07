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
    if (null == userHome || userHome.isBlank()) {
      // No home directory is known (it can be absent in a native image); leave a leading '~' literal
      // rather than crashing in Matcher.quoteReplacement(null).
      return value;
    }
    return value.replaceAll("(^|[:=])~(?=[/:" + tildeFollowers() + "]|$)", "$1" + Matcher.quoteReplacement(userHome));
  }

  // The single expansion pass. Each step consumes one construct from the ORIGINAL value and appends its
  // replacement; nothing already appended is ever revisited, so a substitution's output cannot trigger a
  // further substitution. An unbalanced opener appends the rest of the value and ends the scan.
  private String scan(final String value) {
    final var result = new StringBuilder();
    var index = 0;
    while (index < value.length()) {
      final var character = value.charAt(index);
      if (introduces(value, index, '(')) {
        index = appendCommandSubstitution(value, index, index + 2, matchingParenthesis(value, index + 2), result);
      } else if (introduces(value, index, '{')) {
        index = appendBraceExpression(value, index, result);
      } else if (introducesName(value, index)) {
        index = appendParameter(value, index, result);
      } else if ('`' == character && backticksAreCommands()) {
        index = appendCommandSubstitution(value, index, index + 1, value.indexOf('`', index + 1), result);
      } else {
        result.append(character);
        index++;
      }
    }
    return result.toString();
  }

  // Whether a '$<opener>' construct (a '$(' command substitution or a '${' brace expression) begins at
  // 'index'.
  private static boolean introduces(final String value, final int index, final char opener) {
    return '$' == value.charAt(index) && index + 1 < value.length() && opener == value.charAt(index + 1);
  }

  // Whether a bare '$NAME' parameter reference begins at 'index'.
  private static boolean introducesName(final String value, final int index) {
    return '$' == value.charAt(index) && index + 1 < value.length() && isNameStart(value.charAt(index + 1));
  }

  // Appends a command substitution whose body runs from 'bodyStart' to the 'close' delimiter and returns
  // the index just past it. A negative 'close' marks an unbalanced opener, so the value from 'open' is
  // emitted literally and the returned length ends the scan rather than re-examining it.
  private int appendCommandSubstitution(final String value, final int open, final int bodyStart,
      final int close, final StringBuilder result) {
    if (close < 0) {
      result.append(value, open, value.length());
      return value.length();
    }
    result.append(commandOutput(value.substring(bodyStart, close)));
    return close + 1;
  }

  // Appends a '${...}' construct: a bare '${NAME}' becomes the parameter's value, while a brace
  // expression ilo does not expand (e.g. '${VAR:-default}') is emitted literally as a whole, so an
  // embedded '$(...)' inside it is not mistaken for a command substitution. A '${' with no closing brace
  // leaves the '$' literal and resumes after it.
  private int appendBraceExpression(final String value, final int index, final StringBuilder result) {
    final var end = value.indexOf('}', index + 2);
    if (end < 0) {
      result.append(value.charAt(index));
      return index + 1;
    }
    if (isParameterName(value, index + 2, end)) {
      result.append(parameterValue("$" + value.substring(index + 2, end)));
    } else {
      result.append(value, index, end + 1);
    }
    return end + 1;
  }

  // Appends a bare '$NAME' parameter, consuming the longest run of name characters after the '$'.
  private int appendParameter(final String value, final int index, final StringBuilder result) {
    var end = index + 1;
    while (end < value.length() && isNameChar(value.charAt(end))) {
      end++;
    }
    result.append(parameterValue(value.substring(index, end)));
    return end;
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
