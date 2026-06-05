/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import wtf.metio.ilo.cli.Executables;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Support for POSIX compatible shells.
 *
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/utilities/V3_chap02.html">Shell Command Language</a>
 */
final class PosixShell extends ParameterExpansion {

  private static final String OLD_COMMAND_STYLE = String.format("`(?<%s>[^`]+)`", MATCHER_GROUP_NAME);
  private static final String PARAMETER_STYLE = String.format("(?<%s>\\$[a-zA-Z][a-zA-Z0-9_]*)", MATCHER_GROUP_NAME);
  private static final String PARAMETER_WITH_BRACES_STYLE = String.format("(?<%s>\\$\\{[a-zA-Z][a-zA-Z0-9_]*})", MATCHER_GROUP_NAME);

  // visible for testing
  static final Pattern OLD_COMMAND_PATTERN = Pattern.compile(OLD_COMMAND_STYLE);
  static final Pattern PARAMETER_WITH_BRACES_PATTERN = Pattern.compile(PARAMETER_WITH_BRACES_STYLE);
  static final Pattern PARAMETER_PATTERN = Pattern.compile(PARAMETER_STYLE);

  private final Path shellBinary;

  PosixShell(final Path shellBinary) {
    this.shellBinary = shellBinary;
  }

  @Override
  public String substituteCommands(final String value) {
    // New-style '$(...)' is scanned with balanced parentheses so nested substitutions survive; the
    // old-style backtick form (which does not nest) is handled by a regex.
    final var newStyle = substituteBalanced(value,
        command -> Executables.runForExpansion(shellBinary.toString(), "-c", command));
    return replace(newStyle,
        command -> Executables.runForExpansion(shellBinary.toString(), "-c", command),
        OLD_COMMAND_PATTERN);
  }

  @Override
  public String expandParameters(final String value) {
    return replace(expandTilde(value),
        parameter -> Executables.runForExpansion("/usr/bin/env", shellBinary.toString(), "-c", parameterCommand(parameter)),
        PARAMETER_WITH_BRACES_PATTERN, PARAMETER_PATTERN);
  }

  // visible for testing
  static String parameterCommand(final String parameter) {
    // '%s' is a literal format and the parameter is double-quoted, so the shell expands it but
    // printf reuses the value verbatim — no word-splitting and no interpretation of '%' in it.
    return "printf '%s' \"" + parameter + "\"";
  }

}
