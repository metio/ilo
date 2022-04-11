/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import wtf.metio.ilo.compose.ComposeCLI;
import wtf.metio.ilo.compose.ComposeRuntime;
import wtf.metio.ilo.compose.ComposeRuntimes;
import wtf.metio.ilo.errors.NoMatchingRuntimeException;
import wtf.metio.ilo.model.CliTool;
import wtf.metio.ilo.model.Runtime;
import wtf.metio.ilo.shell.ShellCLI;
import wtf.metio.ilo.shell.ShellRuntime;
import wtf.metio.ilo.shell.ShellRuntimes;

import java.util.List;
import java.util.Optional;

/**
 * Utility class that automatically selects a runtime based on what is currently installed on the host machine.
 */
public final class AutoSelectRuntime {

  /**
   * Select a runtime for 'ilo shell'. Users can force a certain runtime by specifying the runtime parameter.
   *
   * @param runtime The runtime to force, or null for auto-selection.
   * @return The selected shell runtime.
   */
  public static ShellCLI selectShellRuntime(final ShellRuntime runtime) {
    final var preferredRuntime = Optional.ofNullable(runtime)
      .or(() -> Optional.ofNullable(System.getenv(EnvironmentVariables.ILO_SHELL_RUNTIME.name()))
        .map(ShellRuntime::fromAlias))
      .orElse(null);
    return autoSelect(preferredRuntime, ShellRuntimes.allRuntimes());
  }

  /**
   * Select a runtime for 'ilo compose'. Users can force a certain runtime by specifying the runtime parameter.
   *
   * @param runtime The runtime to force, or null for auto-selection.
   * @return The selected compose runtime.
   */
  public static ComposeCLI selectComposeRuntime(final ComposeRuntime runtime) {
    final var preferredRuntime = Optional.ofNullable(runtime)
      .or(() -> Optional.ofNullable(System.getenv(EnvironmentVariables.ILO_COMPOSE_RUNTIME.name()))
        .map(ComposeRuntime::fromAlias))
      .orElse(null);
    return autoSelect(preferredRuntime, ComposeRuntimes.allRuntimes());
  }

  private static <TOOL extends CliTool<?>> TOOL autoSelect(
    final Runtime runtime,
    final List<TOOL> tools) {
    return tools.stream()
      .filter(CliTool::exists)
      .filter(tool -> Optional.ofNullable(runtime)
        .map(selectedRuntime -> selectedRuntime.matches(tool.name()))
        .orElse(true))
      .findFirst()
      .orElseThrow(NoMatchingRuntimeException::new);
  }

  private AutoSelectRuntime() {
    // factory class
  }

}
