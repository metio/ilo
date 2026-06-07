/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import wtf.metio.ilo.cli.Executables;

import java.nio.file.Path;

/**
 * Support for POSIX compatible shells.
 *
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/utilities/V3_chap02.html">Shell Command Language</a>
 */
final class PosixShell extends ShellExpansion {

  private final Path shellBinary;

  PosixShell(final Path shellBinary) {
    this.shellBinary = shellBinary;
  }

  @Override
  String commandOutput(final String script) {
    return Executables.runForExpansion(shellBinary.toString(), "-c", script);
  }

  @Override
  String parameterValue(final String reference) {
    return Executables.runForExpansion(shellBinary.toString(), "-c", parameterCommand(reference));
  }

  // visible for testing
  static String parameterCommand(final String parameter) {
    // '%s' is a literal format and the parameter is double-quoted, so the shell expands it but
    // printf reuses the value verbatim — no word-splitting and no interpretation of '%' in it.
    return "printf '%s' \"" + parameter + "\"";
  }

}
