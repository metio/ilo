/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.compose;

import wtf.metio.ilo.cli.Terminal;
import wtf.metio.ilo.os.OSSupport;
import wtf.metio.ilo.utils.Strings;

import java.util.List;
import java.util.Optional;

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

  // 'up --detach' is idempotent: it creates the services the first time and starts the existing,
  // stopped ones on later runs, so it serves as both the create and the start step of a session.
  @Override
  public final List<String> createArguments(final ComposeOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name(), command()),
        fromList(expand.expand(options.runtimeOptions)),
        withPrefix("--file", expand.expand(options.file)),
        of("up", "--detach"),
        fromList(expand.expand(options.runtimeRunOptions)),
        optional("", expand.expand(options.service)));
  }

  @Override
  public final List<String> attachArguments(final ComposeOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    final var command = Optional.ofNullable(options.arguments)
        .filter(arguments -> !arguments.isEmpty())
        .map(expand::expand)
        .orElseGet(() -> List.of(expand.expand(shell(options))));
    return flatten(
        of(name(), command()),
        fromList(expand.expand(options.runtimeOptions)),
        withPrefix("--file", expand.expand(options.file)),
        of("exec"),
        // Disable the pseudo-TTY unless ilo is attached to a real terminal; otherwise an interactive
        // attach in a non-interactive session (e.g. CI) would fail with "the input device is not a TTY".
        maybe(!options.interactive || !Terminal.isInteractive(), "-T"),
        optional("", expand.expand(options.service)),
        fromList(command));
  }

  // Stops — but keeps — the services so the next 'up --detach' restarts them instead of recreating.
  @Override
  public final List<String> stopArguments(final ComposeOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name(), command()),
        fromList(expand.expand(options.runtimeOptions)),
        withPrefix("--file", expand.expand(options.file)),
        of("stop"));
  }

  // Removes the services entirely, used to force a clean-slate recreate on the next run.
  @Override
  public final List<String> removeArguments(final ComposeOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name(), command()),
        fromList(expand.expand(options.runtimeOptions)),
        withPrefix("--file", expand.expand(options.file)),
        of("down"));
  }

  private static String shell(final ComposeOptions options) {
    return Strings.isNotBlank(options.shell) ? options.shell : "/bin/sh";
  }

}
