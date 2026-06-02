/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.compose;

import wtf.metio.ilo.cli.Terminal;
import wtf.metio.ilo.os.OSSupport;

import java.util.List;

import static java.util.stream.Stream.of;
import static wtf.metio.ilo.utils.Streams.*;

abstract class DockerComposeLike implements ComposeCLI {

  @Override
  public final List<String> pullArguments(final ComposeOptions options) {
    if (options.pull) {
      final var expand = OSSupport.expander();
      return flatten(
          of(name(), command()),
          fromList(expand.expand(options.runtimeOptions)),
          withPrefix("--file", expand.expand(options.file)),
          of("pull"),
          fromList(expand.expand(options.runtimePullOptions)));
    }
    return List.of();
  }

  @Override
  public final List<String> buildArguments(final ComposeOptions options) {
    if (options.build) {
      final var expand = OSSupport.expander();
      return flatten(
          of(name(), command()),
          fromList(expand.expand(options.runtimeOptions)),
          withPrefix("--file", expand.expand(options.file)),
          of("build"),
          fromList(expand.expand(options.runtimeBuildOptions)));
    }
    return List.of();
  }

  @Override
  public final List<String> runArguments(final ComposeOptions options) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name(), command()),
        fromList(expand.expand(options.runtimeOptions)),
        withPrefix("--file", expand.expand(options.file)),
        of("run"),
        fromList(expand.expand(options.runtimeRunOptions)),
        // Disable the pseudo-TTY unless ilo is attached to a real terminal; otherwise an interactive
        // run in a non-interactive session (e.g. CI) would fail with "the input device is not a TTY".
        maybe(!options.interactive || !Terminal.isInteractive(), "-T"),
        of(expand.expand(options.service)),
        fromList(expand.expand(options.arguments)));
  }

  @Override
  public final List<String> cleanupArguments(final ComposeOptions options) {
    // docker compose needs an additional cleanup even when using 'run --rm'
    // see https://github.com/docker/compose/issues/2791
    final var expand = OSSupport.expander();
    return flatten(
        of(name(), command()),
        fromList(expand.expand(options.runtimeOptions)),
        withPrefix("--file", expand.expand(options.file)),
        of("down"),
        fromList(expand.expand(options.runtimeCleanupOptions)));
  }

}
