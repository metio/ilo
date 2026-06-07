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

  // '--file' names a compose file; it is repeated before each file the step passes.
  private static final String FILE = "--file";

  @Override
  public final List<String> pullArguments(final ComposeOptions options) {
    if (options.pull) {
      final var expand = OSSupport.expander();
      return flatten(
          of(name(), command()),
          fromList(expand.expand(options.runtimeOptions)),
          withPrefix(FILE, expand.expand(options.file)),
          of("pull"),
          fromList(expand.expand(options.runtimePullOptions)),
          // Scoped to the services this session brings up (matching 'up'), so a multi-service project's
          // unrelated services are not pulled.
          optional("", expand.expand(options.service)),
          fromList(expand.expand(options.runServices)));
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
          withPrefix(FILE, expand.expand(options.file)),
          of("build"),
          fromList(expand.expand(options.runtimeBuildOptions)),
          // Scoped to the services this session brings up (matching 'up'), so a multi-service project's
          // unrelated services are not rebuilt.
          optional("", expand.expand(options.service)),
          fromList(expand.expand(options.runServices)));
    }
    return List.of();
  }

  // 'up --detach' is idempotent: it creates the services the first time and starts the existing,
  // stopped ones on later runs, so it serves as both the create and the start step of a session.
  @Override
  public final List<String> createArguments(final ComposeOptions options, final String containerName) {
    return createArguments(options, options.file);
  }

  @Override
  public final List<String> createArguments(final ComposeOptions options, final List<String> composeFiles) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name(), command()),
        fromList(expand.expand(options.runtimeOptions)),
        withPrefix(FILE, expand.expand(composeFiles)),
        of("up", "--detach"),
        fromList(expand.expand(options.runtimeRunOptions)),
        optional("", expand.expand(options.service)),
        // Additional services to start besides the attached one (devcontainer runServices).
        fromList(expand.expand(options.runServices)));
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

  // Stops — but keeps — the services this session started so the next 'up --detach' restarts them
  // instead of recreating. Scoped to the attached service plus its run-services (exactly what
  // 'createArguments' brought up): a bare 'compose stop' would stop the whole project, tearing down
  // services another terminal is still using, while stopping only the attached service would leak the
  // run-services. With no service set it falls back to stopping the whole project.
  @Override
  public final List<String> stopArguments(final ComposeOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name(), command()),
        fromList(expand.expand(options.runtimeOptions)),
        withPrefix("--file", expand.expand(options.file)),
        of("stop"),
        optional("", expand.expand(options.service)),
        fromList(expand.expand(options.runServices)));
  }

  // Lists the attached service's processes so the session can tell whether another terminal is still
  // attached (see ContainerProcesses). 'top' reports the running service's container only.
  @Override
  public final List<String> processesArguments(final ComposeOptions options) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name(), command()),
        fromList(expand.expand(options.runtimeOptions)),
        withPrefix("--file", expand.expand(options.file)),
        of("top"),
        optional("", expand.expand(options.service)));
  }

  // Removes the services entirely (compose 'down'), used to force a clean-slate recreate on the next
  // run. This is the compose teardown/cleanup step, so '--runtime-cleanup-option' values (e.g.
  // '--volumes', '--rmi all') are passed to it.
  @Override
  public final List<String> removeArguments(final ComposeOptions options, final String containerName) {
    final var expand = OSSupport.expander();
    return flatten(
        of(name(), command()),
        fromList(expand.expand(options.runtimeOptions)),
        withPrefix("--file", expand.expand(options.file)),
        of("down"),
        fromList(expand.expand(options.runtimeCleanupOptions)));
  }

  private static String shell(final ComposeOptions options) {
    return Strings.isNotBlank(options.shell) ? options.shell : "/bin/sh";
  }

}
