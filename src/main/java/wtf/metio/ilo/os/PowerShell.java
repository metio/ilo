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

  private static final String PARAMETER_STYLE = String.format("(?<%s>\\$[a-zA-Z][a-zA-Z0-9_]*)", MATCHER_GROUP_NAME);

  // visible for testing
  static final Pattern PARAMETER_PATTERN = Pattern.compile(PARAMETER_STYLE);

  private final Path shellBinary;

  PowerShell(final Path shellBinary) {
    this.shellBinary = shellBinary;
  }

  @Override
  public String substituteCommands(final String value) {
    // '$(...)' is scanned with balanced parentheses so nested substitutions survive.
    return substituteBalanced(value,
        command -> Executables.runForExpansion(shellBinary.toString(), "-OutputFormat", "Text", "-Command", command));
  }

  @Override
  public String expandParameters(final String value) {
    return replace(expandTilde(value),
        parameter -> Executables.runForExpansion(shellBinary.toString(), "-OutputFormat", "Text", "-Command", parameterCommand(parameter)),
        PARAMETER_PATTERN);
  }

  // visible for testing
  static String parameterCommand(final String parameter) {
    // 'parameter' is a POSIX-style reference such as "$HOME"; PowerShell reads an environment variable
    // as "$env:HOME". The reference is passed as a single variable expression (not quoted), so
    // Write-Output emits its value verbatim — spaces and all — and an unset variable yields nothing.
    return "Write-Output $env:" + parameter.substring(1);
  }

}
