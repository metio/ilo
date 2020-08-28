/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.compose.ComposeOptions;
import wtf.metio.ilo.model.ComposeCLI;

import java.util.List;

import static java.util.stream.Stream.of;
import static wtf.metio.ilo.utils.Streams.*;
import static wtf.metio.ilo.utils.Streams.fromList;

abstract class DockerComposePodmanCompose implements ComposeCLI {

  @Override
  public final List<String> pullArguments(final ComposeOptions options) {
    if (options.pull) {
      return flatten(
          of(name()),
          fromList(options.runtimeOptions),
          of("--file", options.file),
          of("pull"),
          fromList(options.runtimePullOptions));
    }
    return List.of();
  }

  @Override
  public final List<String> buildArguments(final ComposeOptions options) {
    if (options.build) {
      return flatten(
          of(name()),
          fromList(options.runtimeOptions),
          of("--file", options.file),
          of("build"),
          fromList(options.runtimeBuildOptions));
    }
    return List.of();
  }

  @Override
  public final List<String> runArguments(final ComposeOptions options) {
    return flatten(
        of(name()),
        fromList(options.runtimeOptions),
        of("--file", options.file),
        of("run"),
        fromList(options.runtimeRunOptions),
        maybe(!options.interactive, "-T"),
        of(options.service),
        fromList(options.arguments));
  }

  @Override
  public final List<String> cleanupArguments(final ComposeOptions options) {
    // docker-compose needs an additional cleanup even when using 'run --rm'
    // see https://github.com/docker/compose/issues/2791
    return flatten(
        of(name()),
        fromList(options.runtimeOptions),
        of("--file", options.file),
        of("down"),
        fromList(options.runtimeCleanupOptions));
  }

}
