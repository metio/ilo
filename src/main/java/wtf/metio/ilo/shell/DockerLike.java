/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import wtf.metio.ilo.cli.Executables;
import wtf.metio.ilo.os.OSSupport;
import wtf.metio.ilo.utils.Strings;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Stream.of;
import static wtf.metio.ilo.utils.Streams.*;

abstract class DockerLike implements ShellCLI {

  // The runtimes restrict 'ps' output to containers matching the value that follows each '--filter'.
  private static final String FILTER = "--filter";
  // '--format' takes a Go-template that selects which container fields a query prints.
  private static final String FORMAT = "--format";

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
    return ShellArguments.build(name(), options);
  }

  @Override
  public final List<String> probeArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    // The runtimes treat 'name=' as a regex, so the '.' a slug may contain is escaped to match
    // literally and the anchors pin it to this exact container.
    final var nameFilter = "name=^" + containerName.replace(".", "\\.") + "$";
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("ps", "--all", FILTER, nameFilter, FORMAT, "{{.State}}"));
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
    return ShellArguments.create(name(), options, containerName,
        projectMount(options, currentDir, workingDir, expand).toList(), supportsHostname());
  }

  // The mount that puts the project directory into the container. An explicit --workspace-mount replaces
  // it entirely. Otherwise the project directory is bind-mounted onto the working directory: on POSIX
  // hosts as 'source:target:z' (the ':z' relabels for SELinux), but on Windows via the explicit
  // '--mount' fields, because a Windows source path contains a drive colon that the 'source:target'
  // form would mis-parse.
  private static Stream<String> projectMount(final ShellOptions options, final String currentDir,
      final String workingDir, final OSSupport.Expander expand) {
    if (Strings.isNotBlank(options.workspaceMount)) {
      return of("--mount", expand.expand(options.workspaceMount));
    }
    if (!options.mountProjectDir) {
      return Stream.empty();
    }
    if (Executables.isWindows()) {
      return of("--mount", "type=bind,source=" + currentDir + ",target=" + workingDir);
    }
    return of("--volume", currentDir + ":" + workingDir + ":z");
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
    return ShellArguments.attach(name(), options, containerName);
  }

  @Override
  public final List<String> execArguments(final ShellOptions options, final String containerName, final List<String> command) {
    return ShellArguments.exec(name(), options, containerName, command);
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
        of(FORMAT, "{{.Names}}"));
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
  public final List<String> mainPidArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("inspect", FORMAT, "{{.State.Pid}}", containerName));
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
