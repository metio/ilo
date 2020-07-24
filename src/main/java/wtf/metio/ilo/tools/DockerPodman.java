/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.compose.ComposeOptions;
import wtf.metio.ilo.shell.ShellOptions;

import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

final class DockerPodman {

  public static List<String> arguments(final ShellOptions options, final String tool) {
    final var currentDir = System.getProperty("user.dir");
    final var run = Stream.of(
        tool,
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
    final var args = Stream.of(run, projectDir, tty, command)
        .flatMap(identity())
        .collect(toList());
    Debug.showExecutedCommand(options.debug, args);
    return args;
  }

  public static List<String> arguments(final ComposeOptions options, final String tool) {
    final var run = Stream.of(
        tool,
        "--file", options.composeFile,
        "run",
        "--rm"
    );
    final var tty = options.interactive ? Stream.<String>empty() : Stream.of("-T");
    final var service = Stream.of(options.service);
    final var args = Stream.of(run, tty, service)
        .flatMap(identity())
        .collect(toList());
    Debug.showExecutedCommand(options.debug, args);
    return args;
  }

  public static List<String> cleanupArguments(final ComposeOptions options, final String tool) {
    final var args = List.of(tool, "--file", options.composeFile, "down");
    Debug.showExecutedCommand(options.debug, args);
    return args;
  }

}
