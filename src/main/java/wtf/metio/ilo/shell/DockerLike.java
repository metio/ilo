/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

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
      return flatten(
          of(name()),
          fromList(OSSupport.expand(options.runtimeOptions)),
          of("pull"),
          fromList(OSSupport.expand(options.runtimePullOptions)),
          of(OSSupport.expand(options.image)));
    }
    return List.of();
  }

  @Override
  public final List<String> buildArguments(final ShellOptions options) {
    if (Strings.isNotBlank(options.containerfile)) {
      return flatten(
          of(name()),
          fromList(OSSupport.expand(options.runtimeOptions)),
          of("build", "--file", options.containerfile),
          fromList(OSSupport.expand(options.runtimeBuildOptions)),
          maybe(options.pull, "--pull"),
          of("--tag", OSSupport.expand(options.image)),
          of(OSSupport.expand(options.context)));
    }
    return List.of();
  }

  @Override
  public final List<String> runArguments(final ShellOptions options) {
    final var currentDir = System.getProperty("user.dir");
    final var workingDir = Optional.ofNullable(options.workingDir)
        .filter(Strings::isNotBlank)
        .orElse(currentDir);
    final var projectDir = maybe(options.mountProjectDir,
        "--volume", currentDir + ":" + workingDir + ":z");
    return flatten(
        of(name()),
        fromList(OSSupport.expand(options.runtimeOptions)),
        of("run", "--rm"),
        fromList(OSSupport.expand(options.runtimeRunOptions)),
        projectDir,
        of("--workdir", workingDir),
        maybe(options.interactive, "--interactive", "--tty"),
        withPrefix("--env", OSSupport.expand(options.variables)),
        optional("--hostname", OSSupport.expand(options.hostname)),
        withPrefix("--publish", OSSupport.expand(options.ports)),
        withPrefix("--volume", options.missingVolumes.handleLocalDirectories(OSSupport.expand(options.volumes))),
        of(OSSupport.expand(options.image)),
        fromList(OSSupport.expand(options.commands)));
  }

  @Override
  public final List<String> cleanupArguments(final ShellOptions options) {
    if (options.removeImage) {
      return flatten(
          of(name()),
          fromList(OSSupport.expand(options.runtimeOptions)),
          of("rmi"),
          fromList(OSSupport.expand(options.runtimeCleanupOptions)),
          of(OSSupport.expand(options.image)));
    }
    return List.of();
  }

}
