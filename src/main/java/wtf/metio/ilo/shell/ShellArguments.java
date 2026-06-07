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

/**
 * Argument assembly shared by every shell runtime regardless of whether its CLI is docker-flag-
 * compatible. {@code build}, {@code run} (create), the interactive {@code exec} (attach) and a plain
 * {@code exec} take the same flags on podman/nerdctl/docker and Apple's {@code container}; each runtime
 * supplies only the parts that genuinely differ (image-management verbs, the listing/probe commands,
 * and the project-mount form, which the caller passes in).
 */
final class ShellArguments {

  // '--env' sets an environment variable on the container ('run') or on an exec'd process.
  private static final String ENV = "--env";

  static List<String> build(final String name, final ShellOptions options) {
    if (Strings.isNotBlank(options.containerfile)) {
      final var expand = OSSupport.expander();
      return flatten(
          of(name),
          fromList(expand.expand(options.runtimeOptions)),
          of("build", "--file", expand.expand(options.containerfile)),
          fromList(expand.expand(options.runtimeBuildOptions)),
          maybe(options.pull, "--pull"),
          of("--tag", expand.expand(options.image)),
          of(expand.expand(options.context)));
    }
    return List.of();
  }

  static List<String> create(final String name, final ShellOptions options, final String containerName,
      final List<String> projectMount) {
    final var expand = OSSupport.expander();
    final var currentDir = System.getProperty("user.dir");
    final var workingDir = Optional.ofNullable(options.workingDir)
        .filter(Strings::isNotBlank)
        .orElse(currentDir);
    // picocli supplies a default, but a directly-constructed ShellOptions may leave this null.
    final var missingVolumes = Optional.ofNullable(options.missingVolumes).orElse(ShellVolumeBehavior.CREATE);
    return flatten(
        of(name),
        fromList(expand.expand(options.runtimeOptions)),
        of("run", "--detach", "--name", containerName),
        // Label the container so it can be found and cleaned up later, e.g. by the probe and the
        // stale-container sweep.
        of("--label", "ilo.managed=true", "--label", "ilo.project=" + currentDir),
        fromList(expand.expand(options.runtimeRunOptions)),
        fromList(projectMount),
        of("--workdir", workingDir),
        of(ENV, "ILO_CONTAINER=true"),
        withPrefix(ENV, expand.expand(options.variables)),
        optional("--hostname", expand.expand(options.hostname)),
        withPrefix("--publish", expand.expand(options.ports)),
        withPrefix("--volume", missingVolumes.handleLocalDirectories(expand.expand(options.volumes))),
        fromList(options.userMapping.createArguments(options.remoteUser, options.remoteUid, options.remoteGid, expand)),
        // With the override on, the keepalive replaces the image's entrypoint and command; with it
        // off, neither is set and the image's own long-running process keeps the container alive.
        maybe(options.overrideCommand, "--entrypoint", Keepalive.ENTRYPOINT),
        of(expand.expand(options.image)),
        maybe(options.overrideCommand, "-c", Keepalive.SCRIPT));
  }

  static List<String> attach(final String name, final ShellOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    final var shell = Strings.isNotBlank(options.shell) ? options.shell : "/bin/sh";
    // An explicit command is passed verbatim — not host-expanded — so any variables, globs or '$(...)'
    // in it resolve inside the container where they are meant to apply (matching the lifecycle exec
    // path). Without one, attach the configured shell plus any shell arguments — userEnvProbe maps to
    // '-l'/'-i' here so the user's login/interactive shell profile is sourced.
    final var command = Optional.ofNullable(options.commands)
        .filter(commands -> !commands.isEmpty())
        .orElseGet(() -> flatten(of(expand.expand(shell)), fromList(expand.expand(options.shellArguments))));
    return flatten(
        of(name),
        fromList(expand.expand(options.runtimeOptions)),
        of("exec"),
        // remoteEnv applies to processes the tool runs in the container — the interactive shell here —
        // so it is set on 'exec', not baked onto the container like --env.
        withPrefix(ENV, expand.expand(options.remoteVariables)),
        fromList(options.userMapping.execArguments(options.remoteUser, expand)),
        maybe(options.interactive, "--interactive"),
        // A pseudo-TTY is only allocated when ilo is attached to a real terminal; otherwise an
        // interactive attach in a non-interactive session (e.g. CI) would fail with "the input
        // device is not a TTY".
        maybe(options.interactive && Terminal.isInteractive(), "--tty"),
        of(containerName),
        fromList(command));
  }

  static List<String> exec(final String name, final ShellOptions options, final String containerName,
      final List<String> command) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name),
        fromList(expand.expand(options.runtimeOptions)),
        of("exec"),
        // Lifecycle commands run as remote processes too, so they see remoteEnv as well.
        withPrefix(ENV, expand.expand(options.remoteVariables)),
        of(containerName),
        fromList(command));
  }

  private ShellArguments() {
    // utility class
  }

}
