/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import picocli.CommandLine;
import wtf.metio.ilo.cli.ContainerProcesses;
import wtf.metio.ilo.cli.ContainerState;
import wtf.metio.ilo.cli.SessionLifecycle;
import wtf.metio.ilo.model.CliExecutor;
import wtf.metio.ilo.version.VersionProvider;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;

@CommandLine.Command(
    name = "shell",
    description = "Opens an (interactive) shell for your build environment",
    versionProvider = VersionProvider.class,
    mixinStandardHelpOptions = true,
    showAtFileInUsageHelp = true,
    usageHelpAutoWidth = true,
    showDefaultValues = true,
    descriptionHeading = "%n",
    parameterListHeading = "%n"
)
public final class ShellCommand implements Callable<Integer> {

  @CommandLine.Mixin
  public ShellOptions options;

  // Produces the lifecycle commands to run inside the session's container, given the resolved tool
  // and the derived container name. Plain 'ilo shell' has none; 'ilo devcontainer' supplies the
  // devcontainer's onCreate/postStart/postAttach commands here without coupling shell to that type.
  private BiFunction<ShellCLI, String, SessionLifecycle.Lifecycle> lifecycle =
      (tool, containerName) -> SessionLifecycle.Lifecycle.none();

  private final CliExecutor<? super ShellRuntime, ShellCLI, ShellOptions> executor;

  // default constructor for picocli
  public ShellCommand() {
    this(new ShellExecutor());
  }

  // constructor for testing
  ShellCommand(final CliExecutor<? super ShellRuntime, ShellCLI, ShellOptions> executor) {
    this.executor = executor;
  }

  // 'ilo devcontainer' injects the in-container lifecycle commands here before calling.
  public void lifecycle(final BiFunction<ShellCLI, String, SessionLifecycle.Lifecycle> lifecycle) {
    this.lifecycle = lifecycle;
  }

  @Override
  public Integer call() {
    warnIfNonInteractiveWithoutCommand(options);
    final var tool = executor.selectRuntime(options.runtime);
    // Resolve the file-ownership mapping (and, on rootful Docker, build the derived remap image) before
    // the container name is derived, so toggling it recreates the container rather than reusing one
    // created with a different user mapping.
    RemoteUser.resolve(tool, options, executor::capture);
    final var projectDir = System.getProperty("user.dir");
    final var containerName = ShellContainer.name(options, projectDir);
    sweepStaleContainers(tool, projectDir, containerName);
    // Probe the container state once and reuse it for every decision below as well as the session run,
    // rather than probing again inside the lifecycle.
    final var state = executor.probe().state(tool.probeArguments(options, containerName));
    // '--pull' has to recreate the container: a reused container would never see the freshly pulled
    // image, so the flag would otherwise do nothing. A paused container is recreated too, since it
    // cannot be started.
    final var wantsFresh = options.fresh || options.pull || ContainerState.PAUSED == state;
    // Recreating removes the container, which would tear it out from under another terminal attached to
    // the same running container. When that is the case, skip the recreate and attach to the existing
    // container instead — recreating it later, once the other sessions have closed.
    final var deferredForReuse = wantsFresh && ContainerState.RUNNING == state
        && otherSessionsAttached(tool, containerName);
    if (deferredForReuse) {
      System.err.println("ilo: another session is attached to this container, so --fresh/--pull is "
          + "skipped and ilo attaches to the existing one; close the other sessions and rerun to recreate it.");
    }
    final var fresh = wantsFresh && !deferredForReuse;
    final var creating = fresh || ContainerState.ABSENT == state;
    // A keep-id mapping pinned to the user's UID/GID needs the user's in-image IDs, read by probing the
    // image. That probe only matters when the container is created — a reused one already has them baked
    // in — so it is deferred to here rather than run on every invocation.
    RemoteUser.pinKeepId(tool, options, executor::capture, creating);
    final var steps = new SessionLifecycle.Steps(
        // When starting fresh, only remove a container that actually exists — running 'rm' against an
        // absent one would print a spurious "no such container" error on every clean-slate first run.
        fresh && ContainerState.ABSENT != state ? tool.removeArguments(options, containerName) : List.of(),
        tool.pullArguments(options),
        tool.buildArguments(options),
        // Only assemble the create command when the container is actually being created — the lifecycle
        // executes it only then anyway, and assembling it has side effects (missing-volume handling
        // creates/warns/errors), which must not fire on a plain reuse/attach.
        creating ? tool.createArguments(options, containerName) : List.of(),
        tool.startArguments(options, containerName),
        tool.attachArguments(options, containerName),
        () -> teardown(tool, containerName));
    return SessionLifecycle.run(steps, lifecycle.apply(tool, containerName),
        fresh, options.debug, executor.sessionExecutor(), state);
  }

  // A non-interactive attach with no command execs the shell with no stdin attached, so it reads EOF
  // and exits immediately — a session that looks successful but does nothing. Warn so the user knows to
  // pass a command. ('ilo shell' defaults --interactive to true, so this only fires on --no-interactive.)
  // visible for testing
  static void warnIfNonInteractiveWithoutCommand(final ShellOptions options) {
    if (!options.interactive && (options.commands == null || options.commands.isEmpty())) {
      System.err.println("ilo: --no-interactive was given without a command, so the shell starts with no "
          + "input and exits immediately; pass a command to run inside the container.");
    }
  }

  // Keeps only the container matching the current definition: removes this project's stopped
  // containers left behind by earlier configurations, so changing the image, Containerfile, or run
  // options recreates rather than reuses — without leaving the old containers piling up.
  private void sweepStaleContainers(final ShellCLI tool, final String projectDir, final String keep) {
    executor.capture(tool.staleContainersArguments(options, projectDir)).lines()
        .map(String::strip)
        .filter(name -> !name.isBlank() && !name.equals(keep))
        .forEach(name -> executor.execute(tool.removeArguments(options, name), options.debug));
  }

  // Computed after the attach returns, so it reflects whoever is still attached. While another
  // terminal has the container open the environment is left running; once this is the last session
  // out, the container is stopped but kept for reuse, or — with '--remove-image' — removed along with
  // its image to restore clean-slate-every-run.
  private List<List<String>> teardown(final ShellCLI tool, final String containerName) {
    if (otherSessionsAttached(tool, containerName)) {
      if (options.removeImage) {
        System.err.println("ilo: another session is still attached, so --remove-image is skipped; "
            + "the container and its image are kept for the other session.");
      }
      return List.of();
    }
    if (options.removeImage) {
      return List.of(tool.removeArguments(options, containerName), tool.cleanupArguments(options));
    }
    // Leaving the container running keeps its background processes alive and makes the next attach
    // instant; an explicit '--remove-image' still wins over it.
    if (options.keepRunningOnExit) {
      return List.of();
    }
    return List.of(tool.stopArguments(options, containerName));
  }

  // A container is shared across terminals, so it must outlive this session if another is still
  // attached. The runtime is the source of truth: any process besides the keepalive is another open
  // session. This session's own attach has already returned by the time the teardown is computed, so
  // it is not counted. With the keepalive in use its command marker identifies the container's own
  // processes; without it (--no-override-command) the container's main process is identified by its
  // inspected host PID instead, so the ref-count works whatever PIDs the runtime's 'top' reports.
  private boolean otherSessionsAttached(final ShellCLI tool, final String containerName) {
    final var mainPid = options.overrideCommand
        ? ""
        : executor.capture(tool.mainPidArguments(options, containerName));
    return ContainerProcesses.hasSessions(
        executor.capture(tool.processesArguments(options, containerName)), mainPid);
  }

}
