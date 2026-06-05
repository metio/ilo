/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import wtf.metio.ilo.cli.Executables;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static wtf.metio.ilo.utils.Streams.filter;
import static wtf.metio.ilo.utils.Streams.fromList;

public final class OSSupport {

  /**
   * Creates an {@link Expander} bound to the host shell. Detecting the shell scans {@code $PATH}, so
   * a caller expanding several values should obtain one expander and reuse it rather than calling
   * this per value.
   *
   * @return An expander for the detected host shell.
   */
  public static Expander expander() {
    return expander(OSSupport::expansion);
  }

  // visible for testing
  static Expander expander(final Supplier<? extends ParameterExpansion> shellDetector) {
    return new Expander(shellDetector.get());
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

  /**
   * Expands option values against a single, already-detected host shell. The shell is detected once
   * when the expander is created, so reusing one expander across many values avoids re-scanning
   * {@code $PATH} for each one.
   */
  public static final class Expander {

    private final ParameterExpansion expansion;

    // visible for testing
    Expander(final ParameterExpansion expansion) {
      this.expansion = expansion;
    }

    public List<String> expand(final List<String> values) {
      return filter(fromList(values))
          .map(this::expand)
          .collect(toList());
    }

    public String expand(final String value) {
      return Optional.ofNullable(value)
          .map(expansion::expand)
          .orElse(value);
    }

  }

}
