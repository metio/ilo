/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.cli.Terminal;
import wtf.metio.ilo.os.OSSupport;
import wtf.metio.ilo.utils.Strings;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Stream.of;
import static wtf.metio.ilo.utils.Streams.*;

abstract class DockerLike implements ShellCLI {

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
  public final List<String> runArguments(final ShellOptions options) {
    final var expand = OSSupport.expander();
    final var currentDir = System.getProperty("user.dir");
    final var workingDir = Optional.ofNullable(options.workingDir)
        .filter(Strings::isNotBlank)
        .orElse(currentDir);
    final var projectDir = maybe(options.mountProjectDir,
        "--volume", currentDir + ":" + workingDir + ":z");
    return flatten(
        of(name()),
        fromList(expand.expand(options.runtimeOptions)),
        of("run", "--rm"),
        fromList(expand.expand(options.runtimeRunOptions)),
        projectDir,
        of("--workdir", workingDir),
        maybe(options.interactive, "--interactive"),
        // A pseudo-TTY is only allocated when ilo is attached to a real terminal; otherwise a
        // non-interactive run (e.g. 'ilo shell <image> <command>' in CI) would fail with
        // "the input device is not a TTY".
        maybe(options.interactive && Terminal.isInteractive(), "--tty"),
        of("--env", "ILO_CONTAINER=true"),
        withPrefix("--env", expand.expand(options.variables)),
        optional("--hostname", expand.expand(options.hostname)),
        withPrefix("--publish", expand.expand(options.ports)),
        withPrefix("--volume", options.missingVolumes.handleLocalDirectories(expand.expand(options.volumes))),
        of(expand.expand(options.image)),
        fromList(expand.expand(options.commands)));
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
