/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import picocli.CommandLine;
import wtf.metio.ilo.exec.Executables;
import wtf.metio.ilo.tools.Tools;

import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "shell",
    description = "Opens an (interactive) shell for your build environment",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true,
    showDefaultValues = true,
    descriptionHeading = "%n",
    parameterListHeading = "%n"
)
public class Shell implements Callable<Integer> {

  @CommandLine.Mixin
  public ShellOptions options;

  @Override
  public Integer call() {
    return Tools.detectedShellRuntime(options.runtime)
        .map(tool -> tool.arguments(options))
        .map(Executables::runAndWaitForExit)
        .orElse(CommandLine.ExitCode.USAGE);
  }

}
