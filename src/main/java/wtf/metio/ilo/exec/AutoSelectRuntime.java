/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.exec;

import wtf.metio.ilo.errors.NoMatchingRuntimeException;
import wtf.metio.ilo.factories.ShellRuntimes;
import wtf.metio.ilo.model.Matcher;
import wtf.metio.ilo.shell.ShellCLI;
import wtf.metio.ilo.shell.ShellRuntime;
import wtf.metio.ilo.tools.CliTool;

import java.util.List;
import java.util.Optional;

public class AutoSelectRuntime {

  public static ShellCLI selectShellRuntime(final ShellRuntime runtime) {
    return autoSelect(runtime, ShellRuntimes.allRuntimes());
  }

  private static <SHELL extends CliTool<?>> SHELL autoSelect(
      final Matcher matcher,
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
