/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.cli.Debug;
import wtf.metio.ilo.model.ShellCLI;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.ilo.utils.Strings;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

abstract class DockerPodman implements ShellCLI {

  @Override
  public final List<String> pullArguments(final ShellOptions options) {
    if (options.pull && Strings.isBlank(options.dockerfile)) {
      final var args = List.of(name(), "pull", options.image);
      Debug.showExecutedCommand(options.debug, args);
      return args;
    }
    return List.of();
  }

  @Override
  public final List<String> buildArguments(final ShellOptions options) {
    if (Strings.isNotBlank(options.dockerfile)) {
      final var args = Stream.of(
          name(),
          "build",
          "--file", options.dockerfile,
          options.pull ? "--pull" : "",
          "--tag", options.image,
          ".")
          .filter(Objects::nonNull)
          .filter(not(String::isBlank))
          .collect(toList());
      Debug.showExecutedCommand(options.debug, args);
      return args;
    }
    return List.of();
  }

  @Override
  public final List<String> runArguments(final ShellOptions options) {
    final var currentDir = System.getProperty("user.dir");
    final var run = Stream.of(
        name(),
        "run",
        "--rm"
    );
    final var projectDir = options.mountProjectDir ? Stream.of(
        "--volume", currentDir + ":" + currentDir + ":Z",
        "--workdir", currentDir
    ) : Stream.<String>empty();
    final var tty = options.interactive ? Stream.of(
        "--interactive",
        "--tty"
    ) : Stream.<String>empty();
    final var command = Stream.ofNullable(options.commands).flatMap(List::stream);
    final var args = Stream.of(run, projectDir, tty, Stream.of(options.image), command)
        .flatMap(identity())
        .filter(Objects::nonNull)
        .filter(not(String::isBlank))
        .collect(toList());
    Debug.showExecutedCommand(options.debug, args);
    return args;
  }

  @Override
  public final List<String> cleanupArguments(final ShellOptions options) {
    if (options.removeImage) {
      final var args = List.of(name(), "rmi", options.image);
      Debug.showExecutedCommand(options.debug, args);
      return args;
    }
    return List.of();
  }

}
