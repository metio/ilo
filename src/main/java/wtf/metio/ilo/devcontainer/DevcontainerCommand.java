/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.devcontainer;

import picocli.CommandLine;
import wtf.metio.devcontainer.Command;
import wtf.metio.devcontainer.Devcontainer;
import wtf.metio.ilo.cli.ContainerNaming;
import wtf.metio.ilo.cli.Executables;
import wtf.metio.ilo.cli.SessionLifecycle;
import wtf.metio.ilo.compose.ComposeCommand;
import wtf.metio.ilo.errors.DevcontainerJsonMissingException;
import wtf.metio.ilo.errors.RuntimeIOException;
import wtf.metio.ilo.os.OSSupport;
import wtf.metio.ilo.shell.ShellCLI;
import wtf.metio.ilo.shell.ShellCommand;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.ilo.utils.Streams;
import wtf.metio.ilo.utils.Strings;
import wtf.metio.ilo.version.VersionProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
    warnAboutUnsupportedFields(devcontainer);

    if (options.executeInitializeCommand && Objects.nonNull(devcontainer.initializeCommand())) {
      final var exitCode = runCommand(devcontainer.initializeCommand(), options.debug);
      if (CommandLine.ExitCode.OK != exitCode) {
        return exitCode;
      }
    }

    if (isComposeBased(devcontainer)) {
      final var command = new ComposeCommand();
      command.options = composeOptions(options, devcontainer, json);
      return command.call();
    } else if (usesImageOrDockerfile(devcontainer)) {
      final var command = new ShellCommand();
      final var shellOptions = shellOptions(options, devcontainer);
      // A Dockerfile-only devcontainer declares no image name; give the build a stable tag to produce
      // and run from. An explicit image is kept as-is.
      shellOptions.image = imageTag(devcontainer, json);
      // Fold the devcontainer.json into the container's identity so editing it — a lifecycle command,
      // a mount, anything — recreates the container on the next run and re-runs the creation lifecycle.
      shellOptions.identitySource(definition(json));
      command.options = shellOptions;
      command.lifecycle((tool, containerName) -> lifecycle(tool, containerName, shellOptions, devcontainer));
      return command.call();
    }

    System.err.println("ilo: this devcontainer.json declares neither an image, a build.dockerfile, nor a "
        + "dockerComposeFile, so there is nothing for ilo to open.");
    return CommandLine.ExitCode.USAGE;
  }

  // The shell path handles a devcontainer described by an explicit image or by a Dockerfile build; a
  // definition with neither (and no dockerComposeFile) is not something ilo can open.
  // visible for testing
  static boolean usesImageOrDockerfile(final Devcontainer devcontainer) {
    return Strings.isNotBlank(devcontainer.image())
        || (Objects.nonNull(devcontainer.build()) && Strings.isNotBlank(devcontainer.build().dockerfile()));
  }

  // The image to tag and run: the declared image when set, otherwise a stable synthetic tag derived
  // from the devcontainer.json's location so the same project reuses the same built image.
  // visible for testing
  static String imageTag(final Devcontainer devcontainer, final Path json) {
    return Strings.isNotBlank(devcontainer.image())
        ? devcontainer.image()
        : ContainerNaming.containerName("ilo-devcontainer", json.toString());
  }

  // Warns about devcontainer.json fields ilo recognizes but does not act on, so a missing effect is not
  // mistaken for a silent success. Only environment-shaping fields are reported; IDE-only fields such as
  // 'customizations' are left out so the warning stays signal-rich.
  private static void warnAboutUnsupportedFields(final Devcontainer devcontainer) {
    final var ignored = unsupportedFields(devcontainer);
    if (!ignored.isEmpty()) {
      System.err.println("ilo ignores these unsupported devcontainer.json fields: " + String.join(", ", ignored));
    }
  }

  // visible for testing
  static List<String> unsupportedFields(final Devcontainer devcontainer) {
    final var ignored = new ArrayList<String>();
    if (isNotEmpty(devcontainer.features())) {
      ignored.add("features");
    }
    if (isComposeBased(devcontainer)) {
      addComposeDroppedFields(devcontainer, ignored);
    }
    return ignored;
  }

  // The compose path hands container creation to docker-compose/podman-compose, so the devcontainer
  // fields ilo applies itself on the image/Dockerfile path — the in-container lifecycle commands, the
  // environment, the forwarded ports, and the run customizations — have no effect there. They are
  // reported so a dropped effect is not mistaken for a silent success. initializeCommand is left out:
  // it runs on the host before either path, so it is honored regardless.
  private static void addComposeDroppedFields(final Devcontainer devcontainer, final List<String> ignored) {
    if (Objects.nonNull(devcontainer.onCreateCommand())) {
      ignored.add("onCreateCommand");
    }
    if (Objects.nonNull(devcontainer.updateContentCommand())) {
      ignored.add("updateContentCommand");
    }
    if (Objects.nonNull(devcontainer.postCreateCommand())) {
      ignored.add("postCreateCommand");
    }
    if (Objects.nonNull(devcontainer.postStartCommand())) {
      ignored.add("postStartCommand");
    }
    if (Objects.nonNull(devcontainer.postAttachCommand())) {
      ignored.add("postAttachCommand");
    }
    if (isNotEmpty(devcontainer.containerEnv())) {
      ignored.add("containerEnv");
    }
    if (isNotEmpty(devcontainer.remoteEnv())) {
      ignored.add("remoteEnv");
    }
    if (Objects.nonNull(devcontainer.userEnvProbe())) {
      ignored.add("userEnvProbe");
    }
    if (isNotEmpty(devcontainer.forwardPorts())) {
      ignored.add("forwardPorts");
    }
    if (isNotEmpty(devcontainer.appPort())) {
      ignored.add("appPort");
    }
    if (isNotEmpty(devcontainer.runArgs())) {
      ignored.add("runArgs");
    }
    if (isNotEmpty(devcontainer.mounts())) {
      ignored.add("mounts");
    }
  }

  private static boolean isComposeBased(final Devcontainer devcontainer) {
    return Objects.nonNull(devcontainer.dockerComposeFile()) && !devcontainer.dockerComposeFile().isEmpty();
  }

  private static boolean isNotEmpty(final Map<String, ?> map) {
    return Objects.nonNull(map) && !map.isEmpty();
  }

  private static boolean isNotEmpty(final Collection<?> collection) {
    return Objects.nonNull(collection) && !collection.isEmpty();
  }

  // The full devcontainer.json contents, read best-effort, used as extra container-identity material.
  // An unreadable file contributes nothing rather than failing the command.
  // visible for testing
  static String definition(final Path json) {
    try {
      return Files.readString(json);
    } catch (final IOException _) {
      return "";
    }
  }

  // Maps the devcontainer's in-container lifecycle commands onto the session's phases: the creation
  // commands run once when the container is created, postStart on every start, postAttach on every
  // attach. Each is gated by its --execute-* flag. initializeCommand is handled separately above
  // because it runs on the host, before any container exists.
  // visible for testing
  SessionLifecycle.Lifecycle lifecycle(final ShellCLI tool, final String containerName,
      final ShellOptions shellOptions, final Devcontainer devcontainer) {
    final var onCreate = new ArrayList<List<List<String>>>();
    if (options.executeOnCreateCommand) {
      onCreate.addAll(execGroups(tool, containerName, shellOptions, devcontainer.onCreateCommand()));
    }
    if (options.executeUpdateContentCommand) {
      onCreate.addAll(execGroups(tool, containerName, shellOptions, devcontainer.updateContentCommand()));
    }
    if (options.executePostCreateCommand) {
      onCreate.addAll(execGroups(tool, containerName, shellOptions, devcontainer.postCreateCommand()));
    }
    final var onStart = new ArrayList<List<List<String>>>();
    if (options.executePostStartCommand) {
      onStart.addAll(execGroups(tool, containerName, shellOptions, devcontainer.postStartCommand()));
    }
    final var onAttach = new ArrayList<List<List<String>>>();
    if (options.executePostAttachCommand) {
      onAttach.addAll(execGroups(tool, containerName, shellOptions, devcontainer.postAttachCommand()));
    }
    return new SessionLifecycle.Lifecycle(List.copyOf(onCreate), List.copyOf(onStart), List.copyOf(onAttach));
  }

  // Turns one lifecycle command into the parallel step(s) that run it inside the container. A string or
  // array command is a single step with one exec; an object command is a single step whose entries run
  // in parallel, matching the devcontainer spec's object-form semantics. An empty command yields no
  // step at all.
  private List<List<List<String>>> execGroups(final ShellCLI tool, final String containerName,
      final ShellOptions shellOptions, final Command command) {
    final var commands = execLines(tool, containerName, shellOptions, command);
    return commands.isEmpty() ? List.of() : List.of(commands);
  }

  // The 'exec' command lines for a command: a string is handed to 'sh -c' so the container's shell
  // parses it; an array is exec'd verbatim; an object contributes one line per entry.
  private List<List<String>> execLines(final ShellCLI tool, final String containerName,
      final ShellOptions shellOptions, final Command command) {
    if (Objects.isNull(command)) {
      return List.of();
    }
    if (Strings.isNotBlank(command.string())) {
      return List.of(tool.execArguments(shellOptions, containerName, List.of("sh", "-c", command.string())));
    }
    if (Objects.nonNull(command.array()) && !command.array().isEmpty()) {
      return List.of(tool.execArguments(shellOptions, containerName, command.array()));
    }
    if (Objects.nonNull(command.object()) && !command.object().isEmpty()) {
      return command.object().values().stream()
          .flatMap(nested -> execLines(tool, containerName, shellOptions, nested).stream())
          .toList();
    }
    return List.of();
  }

  // visible for testing
  int runCommand(final Command command, final boolean debug) {
    try {
      if (Strings.isNotBlank(command.string())) {
        // The devcontainer spec runs a string-form command "in a shell", so it is handed to the host
        // shell rather than tokenized — otherwise variables, '~' and operators like '&&' would not work.
        return Executables.runAndWaitForExit(OSSupport.shellCommand(command.string()), debug);
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
      System.err.println(rootMessage(exception));
      return CommandLine.ExitCode.USAGE;
    }
  }

  // The message to report for a failed host command, resilient to a missing cause or message: the
  // cause's message is the most specific, then the exception's own, and finally its type so the output
  // is never a bare "null".
  // visible for testing
  static String rootMessage(final Throwable exception) {
    return Stream.of(
            Optional.ofNullable(exception.getCause()).map(Throwable::getMessage).orElse(null),
            exception.getMessage())
        .filter(Objects::nonNull)
        .findFirst()
        .orElseGet(exception::toString);
  }

}
