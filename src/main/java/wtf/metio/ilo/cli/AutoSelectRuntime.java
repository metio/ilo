/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import wtf.metio.ilo.compose.ComposeCLI;
import wtf.metio.ilo.compose.ComposeRuntime;
import wtf.metio.ilo.errors.NoMatchingRuntimeException;
import wtf.metio.ilo.compose.ComposeRuntimes;
import wtf.metio.ilo.shell.ShellRuntimes;
import wtf.metio.ilo.model.Runtime;
import wtf.metio.ilo.shell.ShellCLI;
import wtf.metio.ilo.shell.ShellRuntime;
import wtf.metio.ilo.model.CliTool;

import java.util.List;
import java.util.Optional;

public final class AutoSelectRuntime {

  public static ShellCLI selectShellRuntime(final ShellRuntime runtime) {
    return autoSelect(runtime, ShellRuntimes.allRuntimes());
  }

  public static ComposeCLI selectComposeRuntime(final ComposeRuntime runtime) {
    return autoSelect(runtime, ComposeRuntimes.allRuntimes());
  }

  private static <SHELL extends CliTool<?>> SHELL autoSelect(
      final Runtime runtime,
      final List<SHELL> tools) {
    return tools.stream()
        .filter(CliTool::exists)
        .filter(tool -> Optional.ofNullable(runtime)
            .map(selectedRuntime -> selectedRuntime.matches(tool.name()))
            .orElse(true))
        .findFirst()
        .orElseThrow(NoMatchingRuntimeException::new);
  }

}
