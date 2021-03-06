/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.os.OSSupport;
import wtf.metio.ilo.shell.ShellCLI;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.ilo.utils.Strings;

import java.util.List;

import static java.util.stream.Stream.of;
import static wtf.metio.ilo.utils.Streams.*;

abstract class DockerPodman implements ShellCLI {

  @Override
  public final List<String> pullArguments(final ShellOptions options) {
    if (options.pull && Strings.isBlank(options.dockerfile)) {
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
    if (Strings.isNotBlank(options.dockerfile)) {
      return flatten(
        of(name()),
        fromList(OSSupport.expand(options.runtimeOptions)),
        of("build", "--file", options.dockerfile),
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
    final var projectDir = maybe(options.mountProjectDir,
      "--volume", currentDir + ":" + currentDir + ":Z",
      "--workdir", currentDir);
    final var user = maybe(Strings.isNotBlank(options.runAs),
      "--user", OSSupport.expand(options.runAs));
    final var passwd = maybe(Strings.isNotBlank(options.runAs),
      "--volume", OSSupport.passwdFile(options.runAs) + ":/etc/passwd");
    return flatten(
      of(name()),
      fromList(OSSupport.expand(options.runtimeOptions)),
      of("run", "--rm"),
      fromList(OSSupport.expand(options.runtimeRunOptions)),
      user,
      passwd,
      projectDir,
      maybe(options.interactive, "--interactive", "--tty"),
      withPrefix("--env", OSSupport.expand(options.variables)),
      withPrefix("--publish", OSSupport.expand(options.ports)),
      withPrefix("--volume", OSSupport.expand(options.volumes)),
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
