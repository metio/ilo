/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import picocli.CommandLine;
import wtf.metio.ilo.compose.Compose;
import wtf.metio.ilo.compose.ComposeOptions;
import wtf.metio.ilo.errors.DevcontainerJsonMissingException;
import wtf.metio.ilo.errors.JsonParsingException;
import wtf.metio.ilo.errors.RuntimeIOException;
import wtf.metio.ilo.shell.Shell;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.ilo.utils.Strings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

@CommandLine.Command(
    name = "devcontainer",
    description = "Open an (interactive) shell using devcontainer",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true,
    showDefaultValues = true,
    descriptionHeading = "%n",
    optionListHeading = "%n"
)
public class Devcontainer implements Callable<Integer> {

  @CommandLine.Mixin
  public DevcontainerOptions options;

  @Override
  public Integer call() {
    try {
      final var mapper = new ObjectMapper();
      mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      final var currentDir = Paths.get(System.getProperty("user.dir"));
      final var devcontainerJson = Stream.of(".devcontainer/devcontainer.json", ".devcontainer.json")
          .map(currentDir::resolve)
          .filter(Files::exists)
          .findFirst()
          .orElseThrow(DevcontainerJsonMissingException::new);
      final var json = Files.readString(devcontainerJson);
      final var config = mapper.readValue(json, DevcontainerJson.class);

      if (Strings.isNotBlank(config.dockerComposeFile)) {
        final var opts = new ComposeOptions();
        opts.file = config.dockerComposeFile;
        opts.service = config.service;
        opts.debug = options.debug;
        opts.pull = options.pull;
        opts.runtime = options.composeRuntime;
        final var command = new Compose();
        command.options = opts;
        return command.call();
      }
      if (Strings.isNotBlank(config.image)) {
        final var opts = new ShellOptions();
        opts.image = config.image;
        opts.debug = options.debug;
        opts.pull = options.pull;
        opts.removeImage = options.removeImage;
        opts.runtime = options.shellRuntime;
        opts.dockerfile = config.dockerFile;
        opts.mountProjectDir = options.mountProjectDir;
        final var command = new Shell();
        command.options = opts;
        return command.call();
      }

      return CommandLine.ExitCode.USAGE;
    } catch (final JsonProcessingException exception) {
      throw new JsonParsingException(exception);
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

}
