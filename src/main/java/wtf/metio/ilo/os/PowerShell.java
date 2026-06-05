/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import wtf.metio.ilo.cli.Executables;

import java.nio.file.Path;

/**
 * Support for Windows PowerShell
 *
 * @see <a href="https://microsoft.com/powershell">PowerShell</a>
 */
final class PowerShell extends ShellExpansion {

  private final Path shellBinary;

  PowerShell(final Path shellBinary) {
    this.shellBinary = shellBinary;
  }

  @Override
  String commandOutput(final String script) {
    return Executables.runForExpansion(shellBinary.toString(), "-OutputFormat", "Text", "-Command", script);
  }

  @Override
  String parameterValue(final String reference) {
    return Executables.runForExpansion(shellBinary.toString(), "-OutputFormat", "Text", "-Command", parameterCommand(reference));
  }

  @Override
  boolean backticksAreCommands() {
    // PowerShell uses the backtick as its escape character, not for command substitution.
    return false;
  }

  // visible for testing
  static String parameterCommand(final String parameter) {
    // 'parameter' is a POSIX-style reference such as "$HOME"; PowerShell reads an environment variable
    // as "$env:HOME". The reference is passed as a single variable expression (not quoted), so
    // Write-Output emits its value verbatim — spaces and all — and an unset variable yields nothing.
    return "Write-Output $env:" + parameter.substring(1);
  }

}
