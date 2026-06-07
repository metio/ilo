/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import wtf.metio.ilo.cli.ContainerListing;
import wtf.metio.ilo.cli.ContainerState;
import wtf.metio.ilo.os.OSSupport;
import wtf.metio.ilo.utils.Strings;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Stream.of;
import static wtf.metio.ilo.utils.Streams.*;

/**
 * Apple's {@code container} CLI (macOS, Apple silicon, macOS 26+). Unlike the {@link DockerLike}
 * runtimes its command surface is <em>not</em> docker-flag-compatible, so it implements {@link ShellCLI}
 * directly rather than extending {@code DockerLike}:
 * <ul>
 *   <li>image operations are namespaced ({@code container image pull} / {@code container image delete}),
 *   not the top-level {@code pull} / {@code rmi} the docker family uses;</li>
 *   <li>{@code container list} has no {@code --filter} and only {@code json}/{@code table}/{@code yaml}/
 *   {@code toml} output (no Go templates), so the state probe and stale sweep list every container as
 *   JSON and select entries client-side via {@link ContainerListing};</li>
 *   <li>there is no host-side {@code top} and {@code inspect} exposes no PID, so session ref-counting
 *   (keeping a container alive for a second terminal) is not expressed — see the liveness note below.</li>
 * </ul>
 *
 * <p>This runtime requires {@code container system start} to have run first; ilo does not auto-start
 * it. Being macOS-only, it omits the SELinux relabel and the Windows {@code --mount} form the docker
 * family emits.</p>
 */
public final class Container implements ShellCLI {

  @Override
  public String name() {
    return "container";
  }

  // 'container run' has no --hostname flag (confirmed on macOS 26), so a configured hostname is
  // dropped with a warning instead of emitted.
  @Override
  public boolean supportsHostname() {
    return false;
  }

  // Apple's container is a VM-based runtime like Docker Desktop, whose filesystem layer maps
  // bind-mount ownership, and 'run' has no '--userns' (only '-u/--user', '--uid', '--gid'). So no UID
  // remapping is applied: the container runs as the image's user (by name) and host ownership is left
  // to the runtime — the same NONE mapping ilo uses for rootless Docker / Docker Desktop. Emitting the
  // default KEEP_ID mapping's '--userns=keep-id' here would make 'container run' fail outright.
  // OPEN: whether macOS bind-mount ownership actually lands on the host user is unverified on hardware.
  @Override
  public RemoteUserMapping remoteUserMapping(final boolean enabled, final String remoteUser,
      final Function<List<String>, String> capture) {
    return RemoteUserMapping.NONE;
  }

  @Override
  public List<String> pullArguments(final ShellOptions options) {
    if (options.pull && Strings.isBlank(options.containerfile)) {
      final var expand = OSSupport.expander();
      return flatten(
          of(name()),
          fromList(expand.expand(options.runtimeOptions)),
          // Image management is namespaced: there is no top-level 'container pull'.
          of("image", "pull"),
          fromList(expand.expand(options.runtimePullOptions)),
          of(expand.expand(options.image)));
    }
    return List.of();
  }

  @Override
  public List<String> buildArguments(final ShellOptions options) {
    return ShellArguments.build(name(), options);
  }

  @Override
  public List<String> probeArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    // No name filter and no template format; the whole listing is read and matched in probeState().
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("list", "--all", "--format", "json"));
  }

  @Override
  public ContainerState probeState(final ShellOptions options, final String containerName,
      final Function<List<String>, String> capture) {
    return ContainerListing.stateOf(capture.apply(probeArguments(options, containerName)), containerName);
  }

  @Override
  public List<String> removeArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("delete", "--force", containerName));
  }

  @Override
  public List<String> createArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    final var currentDir = System.getProperty("user.dir");
    final var workingDir = Optional.ofNullable(options.workingDir)
        .filter(Strings::isNotBlank)
        .orElse(currentDir);
    return ShellArguments.create(name(), options, containerName,
        projectMount(options, currentDir, workingDir, expand), supportsHostname());
  }

  // The project mount. An explicit --workspace-mount replaces it. Otherwise the project directory is
  // bind-mounted onto the working directory as 'source:target' — without the docker family's ':z'
  // SELinux relabel (Linux-host-specific) or the Windows '--mount' form, since this runtime is
  // macOS-only.
  private static List<String> projectMount(final ShellOptions options, final String currentDir,
      final String workingDir, final OSSupport.Expander expand) {
    if (Strings.isNotBlank(options.workspaceMount)) {
      return List.of("--mount", expand.expand(options.workspaceMount));
    }
    if (!options.mountProjectDir) {
      return List.of();
    }
    return List.of("--volume", currentDir + ":" + workingDir);
  }

  @Override
  public List<String> startArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("start", containerName));
  }

  @Override
  public List<String> attachArguments(final ShellOptions options, final String containerName) {
    return ShellArguments.attach(name(), options, containerName);
  }

  @Override
  public List<String> execArguments(final ShellOptions options, final String containerName, final List<String> command) {
    return ShellArguments.exec(name(), options, containerName, command);
  }

  @Override
  public List<String> stopArguments(final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("stop", containerName));
  }

  @Override
  public List<String> cleanupArguments(final ShellOptions options) {
    if (options.removeImage) {
      final var expand = OSSupport.expander();
      return flatten(
          of(name()),
          fromList(expand.expand(options.runtimeOptions)),
          // Image management is namespaced: there is no top-level 'container rmi'.
          of("image", "delete"),
          fromList(expand.expand(options.runtimeCleanupOptions)),
          of(expand.expand(options.image)));
    }
    return List.of();
  }

  @Override
  public List<String> staleContainersArguments(final ShellOptions options, final String projectDir) {
    final var expand = OSSupport.expander();
    // No label/status filter; the whole listing is read and filtered in staleContainers().
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("list", "--all", "--format", "json"));
  }

  @Override
  public List<String> staleContainers(final ShellOptions options, final String projectDir,
      final Function<List<String>, String> capture) {
    return ContainerListing.staleNames(capture.apply(staleContainersArguments(options, projectDir)), projectDir);
  }

  // Session ref-counting (whether a second terminal still has the container open) is not supported:
  // this runtime has no host-side 'top' and 'inspect' exposes no PID, so there is no introspection the
  // shared ContainerProcesses logic could read. processesArguments and mainPidArguments are therefore
  // empty, which makes otherSessionsAttached() always report no other session — a single terminal works
  // correctly (its teardown stops the container), while concurrent terminals sharing one container are
  // unsupported (the first to exit stops it). Users who keep a second terminal open can pass
  // '--keep-running-on-exit'.
  // OPEN: enabling multi-terminal sharing would mean introspecting via 'container exec … ps', which
  // requires 'ps' in the image and depends on how this runtime reports an exec'd process's parent PID —
  // both to be verified on hardware before relying on it.

  @Override
  public List<String> processesArguments(final ShellOptions options, final String containerName) {
    return List.of();
  }

  @Override
  public List<String> mainPidArguments(final ShellOptions options, final String containerName) {
    return List.of();
  }

}
