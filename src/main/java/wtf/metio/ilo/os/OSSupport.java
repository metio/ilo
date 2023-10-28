/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.os;

import wtf.metio.ilo.cli.Executables;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static wtf.metio.ilo.utils.Streams.filter;
import static wtf.metio.ilo.utils.Streams.fromList;

public final class OSSupport {

  public static List<String> expand(final List<String> values) {
    return filter(fromList(values))
        .map(OSSupport::expand)
        .collect(toList());
  }

  public static String expand(final String value) {
    final var expansion = expansion();
    return Optional.ofNullable(value)
        .map(expansion::expandParameters)
        .map(expansion::substituteCommands)
        .orElse(value);
  }

  static ParameterExpansion expansion() {
    return posixShell()
        .or(OSSupport::powerShell)
        .orElseGet(NoOpExpansion::new);
  }

  // visible for testing
  static Optional<ParameterExpansion> posixShell() {
    return Executables.of("bash")
        .or(() -> Executables.of("zsh"))
        .or(() -> Executables.of("sh"))
        .map(Path::toAbsolutePath)
        .map(PosixShell::new);
  }

  static Optional<ParameterExpansion> powerShell() {
    return Executables.of("pwsh.exe")
        .or(() -> Executables.of("powershell.exe"))
        .or(() -> Executables.of("pwsh"))
        .map(Path::toAbsolutePath)
        .map(PowerShell::new);
  }

  private OSSupport() {
    // utility class
  }

}
