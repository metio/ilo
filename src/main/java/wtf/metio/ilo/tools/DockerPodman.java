/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.shell.ShellCLI;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.ilo.utils.Strings;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static wtf.metio.ilo.utils.Streams.*;

abstract class DockerPodman implements ShellCLI {

  @Override
  public final List<String> pullArguments(final ShellOptions options) {
    if (options.pull && Strings.isBlank(options.dockerfile)) {
      return flatten(
          of(name()),
          fromList(options.runtimeOptions),
          of("pull"),
          fromList(options.runtimePullOptions),
          of(options.image));
    }
    return List.of();
  }

  @Override
  public final List<String> buildArguments(final ShellOptions options) {
    if (Strings.isNotBlank(options.dockerfile)) {
      return flatten(
          of(name()),
          fromList(options.runtimeOptions),
          of("build", "--file", options.dockerfile),
          fromList(options.runtimeBuildOptions),
          maybe(options.pull, "--pull"),
          of("--tag", options.image),
          of(options.context));
    }
    return List.of();
  }

  @Override
  public final List<String> runArguments(final ShellOptions options) {
    final var currentDir = System.getProperty("user.dir");
    final var projectDir = maybe(options.mountProjectDir,
        "--volume", currentDir + ":" + currentDir + ":Z",
        "--workdir", currentDir);
    return flatten(
        of(name()),
        fromList(options.runtimeOptions),
        of("run", "--rm"),
        fromList(options.runtimeRunOptions),
        projectDir,
        maybe(options.interactive, "--interactive", "--tty"),
        withPrefix("--env", options.variables),
        withPrefix("--publish", options.ports),
        withPrefix("--volume", expandHomeDirectory(options.volumes)),
        of(options.image),
        fromList(options.commands));
  }

  @Override
  public final List<String> cleanupArguments(final ShellOptions options) {
    if (options.removeImage) {
      return flatten(
          of(name()),
          fromList(options.runtimeOptions),
          of("rmi"),
          fromList(options.runtimeCleanupOptions),
          of(options.image));
    }
    return List.of();
  }

  private static Stream<String> withPrefix(final String prefix, final List<String> values) {
    return filter(fromList(values)).flatMap(value -> of(prefix, value));
  }

  private static List<String> expandHomeDirectory(final List<String> values) {
    final var userHome = System.getProperty("user.home");
    return filter(fromList(values))
        .map(value -> value.replace("$HOME", userHome))
        .map(value -> value.replace("~", userHome))
        .collect(toList());
  }

}
