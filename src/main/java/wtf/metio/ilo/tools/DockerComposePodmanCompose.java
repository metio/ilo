/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.cli.Debug;
import wtf.metio.ilo.compose.ComposeOptions;
import wtf.metio.ilo.model.ComposeCLI;

import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

abstract class DockerComposePodmanCompose implements ComposeCLI {

  @Override
  public final List<String> pullArguments(final ComposeOptions options) {
    if (options.pull) {
      final var args = List.of(name(), "pull");
      Debug.showExecutedCommand(options.debug, args);
      return args;
    }
    return List.of();
  }

  @Override
  public final List<String> buildArguments(final ComposeOptions options) {
    return List.of();
  }

  @Override
  public final List<String> runArguments(final ComposeOptions options) {
    final var run = Stream.of(
        name(),
        "--file", options.file,
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

  @Override
  public final List<String> cleanupArguments(final ComposeOptions options) {
    final var args = List.of(name(), "--file", options.file, "down");
    Debug.showExecutedCommand(options.debug, args);
    return args;
  }

}
