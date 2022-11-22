/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.cli.EnvironmentVariables;
import wtf.metio.ilo.errors.NoMatchingRuntimeException;
import wtf.metio.ilo.model.CliTool;
import wtf.metio.ilo.model.Runtime;

import java.util.Arrays;
import java.util.Optional;

public enum ShellRuntime implements Runtime<ShellCLI> {

  PODMAN(new Podman(), "podman", "p"),
  NERDCTL(new Nerdctl(), "nerdctl", "n"),
  DOCKER(new Docker(), "docker", "d");

  private final ShellCLI cli;
  private final String[] aliases;

  ShellRuntime(final ShellCLI cli, final String... aliases) {
    this.cli = cli;
    this.aliases = aliases;
  }

  public static ShellRuntime fromAlias(final String alias) {
    return Runtime.firstMatching(alias, values());
  }

  @Override
  public String[] aliases() {
    return aliases;
  }

  @Override
  public ShellCLI cli() {
    return cli;
  }

  /**
   * Select a runtime for 'ilo shell'.
   *
   * @param preferred The runtime to force, or null for auto-selection.
   * @return The selected compose runtime.
   */
  public static ShellCLI autoSelect(final ShellRuntime preferred) {
    return Optional.ofNullable(preferred)
        .or(() -> Optional.ofNullable(System.getenv(EnvironmentVariables.ILO_SHELL_RUNTIME.name()))
            .map(ShellRuntime::fromAlias))
        .map(ShellRuntime::cli)
        .or(() -> Arrays.stream(ShellRuntime.values())
            .map(ShellRuntime::cli)
            .filter(CliTool::exists)
            .findFirst())
        .filter(CliTool::exists)
        .orElseThrow(NoMatchingRuntimeException::new);
  }

}
