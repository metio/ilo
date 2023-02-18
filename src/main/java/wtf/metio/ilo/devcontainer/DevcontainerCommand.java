/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import picocli.CommandLine;
import wtf.metio.devcontainer.Command;
import wtf.metio.devcontainer.Devcontainer;
import wtf.metio.ilo.cli.Executables;
import wtf.metio.ilo.compose.ComposeCommand;
import wtf.metio.ilo.errors.DevcontainerJsonMissingException;
import wtf.metio.ilo.errors.RuntimeIOException;
import wtf.metio.ilo.os.ShellTokenizer;
import wtf.metio.ilo.shell.ShellCommand;
import wtf.metio.ilo.utils.Streams;
import wtf.metio.ilo.utils.Strings;
import wtf.metio.ilo.version.VersionProvider;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

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

    if (options.executeInitializeCommand && Objects.nonNull(devcontainer.initializeCommand())) {
      final var exitCode = runCommand(devcontainer.initializeCommand(), options.debug);
      if (CommandLine.ExitCode.OK != exitCode) {
        return exitCode;
      }
    }

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

  // visible for testing
  int runCommand(final Command command, final boolean debug) {
    try {
      if (Strings.isNotBlank(command.string())) {
        return Executables.runAndWaitForExit(ShellTokenizer.tokenize(command.string()), debug);
      }
      if (Objects.nonNull(command.array()) && !command.array().isEmpty()) {
        return Executables.runAndWaitForExit(command.array(), debug);
      }
      if (Objects.nonNull(command.object()) && !command.object().isEmpty()) {
        final var futures = command.object().values().stream()
            .map(cmd -> (Supplier<Integer>) () -> runCommand(cmd, debug))
            .map(CompletableFuture::supplyAsync)
            .toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
            .thenApply(ignore -> {
              for (final var future : futures) {
                final var exitCode = future.join();
                if (CommandLine.ExitCode.OK != exitCode) {
                  return exitCode;
                }
              }
              return CommandLine.ExitCode.OK;
            })
            .join();
      }

      return CommandLine.ExitCode.OK;
    } catch (final RuntimeIOException | CompletionException exception) {
      System.err.println(exception.getCause().getMessage());
      return CommandLine.ExitCode.USAGE;
    }
  }

}
