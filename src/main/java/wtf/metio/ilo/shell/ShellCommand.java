/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import picocli.CommandLine;
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
    final var tool = executor.selectRuntime(options.runtime);
    final var projectDir = System.getProperty("user.dir");
    final var containerName = ShellContainer.name(options, projectDir);
    sweepStaleContainers(tool, projectDir, containerName);
    // '--pull' has to recreate the container: a reused container would never see the freshly pulled
    // image, so the flag would otherwise do nothing.
    final var fresh = options.fresh || options.pull;
    final var steps = new SessionLifecycle.Steps(
        tool.probeArguments(options, containerName),
        removeStep(tool, containerName, fresh),
        tool.pullArguments(options),
        tool.buildArguments(options),
        tool.createArguments(options, containerName),
        tool.startArguments(options, containerName),
        tool.attachArguments(options, containerName),
        teardown(tool, containerName));
    return SessionLifecycle.run(steps, lifecycle.apply(tool, containerName),
        fresh, options.debug, executor::execute, executor.probe());
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

  // When starting fresh, only remove a container that actually exists — running 'rm' against an
  // absent one would print a spurious "no such container" error on every clean-slate first run.
  private List<String> removeStep(final ShellCLI tool, final String containerName, final boolean fresh) {
    if (fresh && ContainerState.ABSENT != executor.probe().state(tool.probeArguments(options, containerName))) {
      return tool.removeArguments(options, containerName);
    }
    return List.of();
  }

  // By default a session is stopped but kept for reuse. '--keep-running' leaves it running so other
  // terminals attached to the same container are not interrupted. '--remove-image' opts out of reuse
  // entirely: the container is removed and its image deleted, restoring clean-slate-every-run.
  private List<List<String>> teardown(final ShellCLI tool, final String containerName) {
    if (options.removeImage) {
      return List.of(tool.removeArguments(options, containerName), tool.cleanupArguments(options));
    }
    if (options.keepRunning) {
      return List.of();
    }
    return List.of(tool.stopArguments(options, containerName));
  }

}
