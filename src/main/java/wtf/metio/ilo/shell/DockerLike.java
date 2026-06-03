/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import wtf.metio.ilo.cli.Keepalive;
import wtf.metio.ilo.cli.Terminal;
import wtf.metio.ilo.os.OSSupport;
import wtf.metio.ilo.utils.Strings;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Stream.of;
import static wtf.metio.ilo.utils.Streams.*;

abstract class DockerLike implements ShellCLI {

  // The runtimes restrict 'ps' output to containers matching the value that follows each '--filter'.
  private static final String FILTER = "--filter";

  @Override
  public final List<String> pullArguments(final ShellOptions options) {
    if (options.pull && Strings.isBlank(options.containerfile)) {
      final var expand = OSSupport.expander();
      return flatten(
          of(name()),
          fromList(expand.expand(options.runtimeOptions)),
          of("pull"),
          fromList(expand.expand(options.runtimePullOptions)),
          of(expand.expand(options.image)));
    }
    return List.of();
  }

  @Override
  public final List<String> buildArguments(final ShellOptions options) {
    if (Strings.isNotBlank(options.containerfile)) {
      final var expand = OSSupport.expander();
      return flatten(
          of(name()),
          fromList(expand.expand(options.runtimeOptions)),
          of("build", "--file", expand.expand(options.containerfile)),
          fromList(expand.expand(options.runtimeBuildOptions)),
          maybe(options.pull, "--pull"),
          of("--tag", expand.expand(options.image)),
          of(expand.expand(options.context)));
    }
    return List.of();
  }

  @Override
  public final List<String> probeArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("ps", "--all", FILTER, "name=^" + containerName + "$", "--format", "{{.State}}"));
  }

  @Override
  public final List<String> removeArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("rm", "--force", containerName));
  }

  @Override
  public final List<String> createArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    final var currentDir = System.getProperty("user.dir");
    final var workingDir = Optional.ofNullable(options.workingDir)
        .filter(Strings::isNotBlank)
        .orElse(currentDir);
    final var projectDir = maybe(options.mountProjectDir,
        "--volume", currentDir + ":" + workingDir + ":z");
    // picocli supplies a default, but a directly-constructed ShellOptions may leave this null.
    final var missingVolumes = Optional.ofNullable(options.missingVolumes).orElse(ShellVolumeBehavior.CREATE);
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("run", "--detach", "--name", containerName),
        // Label the container so it can be found and cleaned up later, e.g.
        // 'docker ps --all --filter label=ilo.managed'.
        of("--label", "ilo.managed=true", "--label", "ilo.project=" + currentDir),
        fromList(expand.expand(options.runtimeRunOptions)),
        projectDir,
        of("--workdir", workingDir),
        of("--env", "ILO_CONTAINER=true"),
        withPrefix("--env", expand.expand(options.variables)),
        optional("--hostname", expand.expand(options.hostname)),
        withPrefix("--publish", expand.expand(options.ports)),
        withPrefix("--volume", missingVolumes.handleLocalDirectories(expand.expand(options.volumes))),
        // With the override on, the keepalive replaces the image's entrypoint and command; with it
        // off, neither is set and the image's own long-running process keeps the container alive.
        maybe(options.overrideCommand, "--entrypoint", Keepalive.ENTRYPOINT),
        of(expand.expand(options.image)),
        maybe(options.overrideCommand, "-c", Keepalive.SCRIPT));
  }

  @Override
  public final List<String> startArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("start", containerName));
  }

  @Override
  public final List<String> attachArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    final var shell = Strings.isNotBlank(options.shell) ? options.shell : "/bin/sh";
    final var command = Optional.ofNullable(options.commands)
        .filter(commands -> !commands.isEmpty())
        .map(expand::expand)
        .orElseGet(() -> List.of(expand.expand(shell)));
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("exec"),
        maybe(options.interactive, "--interactive"),
        // A pseudo-TTY is only allocated when ilo is attached to a real terminal; otherwise an
        // interactive attach in a non-interactive session (e.g. CI) would fail with "the input
        // device is not a TTY".
        maybe(options.interactive && Terminal.isInteractive(), "--tty"),
        of(containerName),
        fromList(command));
  }

  @Override
  public final List<String> execArguments(final ShellOptions options, final String containerName, final List<String> command) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("exec", containerName),
        fromList(command));
  }

  @Override
  public final List<String> staleContainersArguments(final ShellOptions options, final String projectDir) {
    final var expand = OSSupport.expander();
    // Each non-running state is listed explicitly; the runtimes OR repeated --filter status values,
    // so a still-running container in another terminal is left out.
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("ps", "--all", FILTER, "label=ilo.project=" + projectDir),
        staleStatuses().stream().flatMap(status -> of(FILTER, "status=" + status)),
        of("--format", "{{.Names}}"));
  }

  // The non-running states swept on reuse. 'dead' (a container whose removal failed) is a Docker-only
  // state; podman and nerdctl reject it as an unknown status filter and fail the whole 'ps', so it is
  // contributed by Docker alone rather than listed here.
  List<String> staleStatuses() {
    return List.of("created", "exited", "paused");
  }

  @Override
  public final List<String> processesArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    // The default 'top' columns include PID and PPID for all three runtimes, which is all the caller
    // needs to distinguish the keepalive from attached sessions.
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("top", containerName));
  }

  @Override
  public final List<String> stopArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("stop", containerName));
  }

  @Override
  public final List<String> cleanupArguments(final ShellOptions options) {
    if (options.removeImage) {
      final var expand = OSSupport.expander();
      return flatten(
          of(name()),
          fromList(expand.expand(options.runtimeOptions)),
          of("rmi"),
          fromList(expand.expand(options.runtimeCleanupOptions)),
          of(expand.expand(options.image)));
    }
    return List.of();
  }

}
