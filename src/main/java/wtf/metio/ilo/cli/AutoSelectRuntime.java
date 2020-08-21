/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import wtf.metio.ilo.model.ComposeCLI;
import wtf.metio.ilo.compose.ComposeRuntime;
import wtf.metio.ilo.errors.NoMatchingRuntimeException;
import wtf.metio.ilo.factories.ComposeRuntimes;
import wtf.metio.ilo.factories.ShellRuntimes;
import wtf.metio.ilo.model.Runtime;
import wtf.metio.ilo.model.ShellCLI;
import wtf.metio.ilo.shell.ShellRuntime;
import wtf.metio.ilo.model.CliTool;

import java.util.List;
import java.util.Optional;

public final class AutoSelectRuntime {

  public static ShellCLI selectShellRuntime(final ShellRuntime runtime) {
    return selectShellRuntime(runtime, ShellRuntimes.allRuntimes());
  }

  static ShellCLI selectShellRuntime(final ShellRuntime runtime, final List<? extends ShellCLI> tools) {
    return autoSelect(runtime, tools);
  }

  public static ComposeCLI selectComposeRuntime(final ComposeRuntime runtime) {
    return selectComposeRuntime(runtime, ComposeRuntimes.allRuntimes());
  }

  static ComposeCLI selectComposeRuntime(final ComposeRuntime runtime, final List<? extends ComposeCLI> tools) {
    return autoSelect(runtime, tools);
  }

  private static <SHELL extends CliTool<?>> SHELL autoSelect(
      final Runtime matcher,
      final List<SHELL> tools) {
    return tools.stream()
        .filter(CliTool::exists)
        .filter(tool -> Optional.ofNullable(matcher)
            .map(runtime -> runtime.matches(tool.name()))
            .orElse(true))
        .findFirst()
        .orElseThrow(NoMatchingRuntimeException::new);
  }

}
