/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import picocli.CommandLine;
import wtf.metio.ilo.compose.Compose;
import wtf.metio.ilo.compose.ComposeOptions;
import wtf.metio.ilo.shell.Shell;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.ilo.utils.Strings;

import java.nio.file.Paths;
import java.util.concurrent.Callable;

import static wtf.metio.ilo.devcontainer.DevcontainerJsonParser.findJson;
import static wtf.metio.ilo.devcontainer.DevcontainerJsonParser.parseJson;

@CommandLine.Command(
    name = "devcontainer",
    description = "Open an (interactive) shell using devcontainer",
    mixinStandardHelpOptions = true,
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

    if (Strings.isNotBlank(devcontainer.dockerComposeFile)) {
      final var opts = new ComposeOptions();
      opts.file = devcontainer.dockerComposeFile;
      opts.service = devcontainer.service;
      opts.debug = options.debug;
      opts.pull = options.pull;
      opts.runtime = options.composeRuntime;
      final var command = new Compose();
      command.options = opts;
      return command.call();
    } else if (Strings.isNotBlank(devcontainer.image)) {
      final var opts = new ShellOptions();
      opts.image = devcontainer.image;
      opts.debug = options.debug;
      opts.pull = options.pull;
      opts.removeImage = options.removeImage;
      opts.runtime = options.shellRuntime;
      opts.dockerfile = devcontainer.dockerFile;
      opts.mountProjectDir = options.mountProjectDir;
      final var command = new Shell();
      command.options = opts;
      return command.call();
    }

    return CommandLine.ExitCode.USAGE;
  }

}
