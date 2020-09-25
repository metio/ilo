/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.os;

import wtf.metio.ilo.cli.Executables;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Support for bourne compatible shells.
 *
 * @see <a href="https://www.gnu.org/software/bash/manual/html_node/Command-Substitution.html">Command Substitution</a>
 * @see <a href="https://www.gnu.org/software/bash/manual/html_node/Shell-Parameter-Expansion.html">Parameter Expansion</a>
 */
final class BourneShell extends ParameterExpansion {

  private static final String NEW_COMMAND_STYLE = String.format("\\$\\((?<%s>[^)]+)\\)", MATCHER_GROUP_NAME);
  private static final String OLD_COMMAND_STYLE = String.format("`(?<%s>[^`]+)`", MATCHER_GROUP_NAME);
  private static final String PARAMETER_STYLE = String.format("(?<%s>\\$[a-zA-Z][a-zA-Z0-9_]*)", MATCHER_GROUP_NAME);
  private static final String PARAMETER_WITH_BRACES_STYLE = String.format("(?<%s>\\$\\{[a-zA-Z][a-zA-Z0-9_]*})", MATCHER_GROUP_NAME);

  // visible for testing
  static final Pattern NEW_COMMAND_PATTERN = Pattern.compile(NEW_COMMAND_STYLE);
  static final Pattern OLD_COMMAND_PATTERN = Pattern.compile(OLD_COMMAND_STYLE);
  static final Pattern PARAMETER_WITH_BRACES_PATTERN = Pattern.compile(PARAMETER_WITH_BRACES_STYLE);
  static final Pattern PARAMETER_PATTERN = Pattern.compile(PARAMETER_STYLE);

  private final Path shellBinary;

  BourneShell(final Path shellBinary) {
    this.shellBinary = shellBinary;
  }

  @Override
  public String substituteCommands(final String value) {
    return replace(value,
      command -> Executables.runAndReadOutput(shellBinary.toString(), "-c", command),
      NEW_COMMAND_PATTERN, OLD_COMMAND_PATTERN);
  }

  @Override
  public String expandParameters(final String value) {
    return replace(expandTilde(value),
      parameter -> Executables.runAndReadOutput(shellBinary.toString(), "-c", "'echo \"" + parameter + "\"'"),
      PARAMETER_WITH_BRACES_PATTERN, PARAMETER_PATTERN);
  }

  private String expandTilde(final String value) {
    final var userHome = System.getProperty("user.home");
    return value.replace("~", userHome);
  }

}
