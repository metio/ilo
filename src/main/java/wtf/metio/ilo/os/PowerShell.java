/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.os;

import wtf.metio.ilo.cli.Executables;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Support for Windows PowerShell
 *
 * @see <a href="https://microsoft.com/powershell">PowerShell</a>
 */
final class PowerShell extends ParameterExpansion {

  private static final String COMMAND_STYLE = String.format("\\$\\((?<%s>[^)]+)\\)", MATCHER_GROUP_NAME);
  private static final String PARAMETER_STYLE = String.format("(?<%s>\\$[a-zA-Z][a-zA-Z0-9_]*)", MATCHER_GROUP_NAME);

  // visible for testing
  static final Pattern COMMAND_PATTERN = Pattern.compile(COMMAND_STYLE);
  static final Pattern PARAMETER_PATTERN = Pattern.compile(PARAMETER_STYLE);

  private final Path shellBinary;

  PowerShell(final Path shellBinary) {
    this.shellBinary = shellBinary;
  }

  @Override
  public String substituteCommands(final String value) {
    return replace(value,
        command -> Executables.runAndReadOutput(shellBinary.toString(), "-OutputFormat", "Text", "-Command", command),
        COMMAND_PATTERN);
  }

  @Override
  public String expandParameters(final String value) {
    return replace(expandTilde(value),
        parameter -> Executables.runAndReadOutput(shellBinary.toString(), "-OutputFormat", "Text", "-Command", "'Write-Output \"" + parameter + "\"'"),
        PARAMETER_PATTERN);
  }

  private String expandTilde(final String value) {
    final var userHome = System.getProperty("user.home");
    return value.replace("~", userHome);
  }

}
