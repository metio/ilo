/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import picocli.CommandLine;
import wtf.metio.devcontainer.Devcontainer;
import wtf.metio.ilo.compose.ComposeCommand;
import wtf.metio.ilo.errors.DevcontainerJsonMissingException;
import wtf.metio.ilo.shell.ShellCommand;
import wtf.metio.ilo.utils.Streams;
import wtf.metio.ilo.utils.Strings;
import wtf.metio.ilo.version.VersionProvider;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.Callable;

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
public final class DevcontainerCommand implements Callable<Integer> {

  @CommandLine.Mixin
  public DevcontainerOptions options;

  @Override
  public Integer call() throws IOException {
    final var currentDir = Paths.get(System.getProperty("user.dir"));
    final var json = Streams.findFirst(currentDir, options.locations)
        .orElseThrow(DevcontainerJsonMissingException::new);
    final var devcontainer = Devcontainer.parse(json);

    if (Objects.nonNull(devcontainer.dockerComposeFile()) && !devcontainer.dockerComposeFile().isEmpty()) {
      final var command = new ComposeCommand();
      command.options = composeOptions(options, devcontainer, json);
      return command.call();
    } else if (Strings.isNotBlank(devcontainer.image())) {
      final var command = new ShellCommand();
      command.options = shellOptions(options, devcontainer);
      return command.call();
    }

    return CommandLine.ExitCode.USAGE;
  }

}
