/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import picocli.CommandLine;
import wtf.metio.ilo.compose.Compose;
import wtf.metio.ilo.shell.Shell;
import wtf.metio.ilo.utils.Strings;
import wtf.metio.ilo.version.VersionProvider;

import java.nio.file.Paths;
import java.util.concurrent.Callable;

import static wtf.metio.ilo.devcontainer.DevcontainerJsonParser.findJson;
import static wtf.metio.ilo.devcontainer.DevcontainerJsonParser.parseJson;
import static wtf.metio.ilo.devcontainer.DevcontainerOptionsMapper.composeOptions;
import static wtf.metio.ilo.devcontainer.DevcontainerOptionsMapper.shellOptions;

@CommandLine.Command(
  name = "devcontainer",
  description = "Open an (interactive) shell using devcontainer",
  versionProvider = VersionProvider.class,
  mixinStandardHelpOptions = true,
  showAtFileInUsageHelp = true,
  usageHelpAutoWidth = true,
  showDefaultValues = true,
  descriptionHeading = "%n",
  optionListHeading = "%n"
)
public final class Devcontainer implements Callable<Integer> {

  @CommandLine.Mixin
  public DevcontainerOptions options;

  @Override
  public Integer call() {
    final var currentDir = Paths.get(System.getProperty("user.dir"));
    final var json = findJson(currentDir);
    final var devcontainer = parseJson(json);

    if (null != devcontainer.dockerComposeFile && !devcontainer.dockerComposeFile.isEmpty()) {
      final var command = new Compose();
      command.options = composeOptions(options, devcontainer, json);
      return command.call();
    } else if (Strings.isNotBlank(devcontainer.image)) {
      final var command = new Shell();
      command.options = shellOptions(options, devcontainer);
      return command.call();
    }

    return CommandLine.ExitCode.USAGE;
  }

}
