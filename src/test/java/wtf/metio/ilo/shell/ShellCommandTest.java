/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import wtf.metio.ilo.cli.ContainerState;
import wtf.metio.ilo.test.TestMethodSources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ShellCommand")
@ExtendWith(SystemStubsExtension.class)
class ShellCommandTest extends TestMethodSources {

  private ShellCommand shell;
  private TestShellExecutor executor;
  private ShellOptions options;

  @BeforeEach
  void setUp() {
    executor = new TestShellExecutor();
    options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.workingDir = "some/dir";
    options.image = "fedora:latest";
    options.shell = "/bin/sh";
    shell = new ShellCommand(executor);
    shell.options = options;
  }

  // The container operation each executed command line performs (the token after the runtime name).
  private List<String> operations() {
    return executor.executed().stream().map(arguments -> arguments.get(1)).toList();
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("creates and attaches when the container is absent")
  void createWhenAbsent(final String runtime) {
    useRuntime(runtime);
    executor.probeState(ContainerState.ABSENT);
    final var exitCode = shell.call();
    assertEquals(0, exitCode);
    assertIterableEquals(List.of("run", "exec", "stop"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("pulls before creating an absent container")
  void pullThenCreate(final String runtime) {
    useRuntime(runtime);
    options.pull = true;
    executor.probeState(ContainerState.ABSENT);
    shell.call();
    assertIterableEquals(List.of("pull", "run", "exec", "stop"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("builds before creating an absent container")
  void buildThenCreate(final String runtime) {
    useRuntime(runtime);
    options.containerfile = "Dockerfile";
    executor.probeState(ContainerState.ABSENT);
    shell.call();
    assertIterableEquals(List.of("build", "run", "exec", "stop"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("starts a stopped container without rebuilding")
  void startWhenStopped(final String runtime) {
    useRuntime(runtime);
    executor.probeState(ContainerState.STOPPED);
    shell.call();
    assertIterableEquals(List.of("start", "exec", "stop"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("attaches directly to a running container")
  void attachWhenRunning(final String runtime) {
    useRuntime(runtime);
    executor.probeState(ContainerState.RUNNING);
    shell.call();
    assertIterableEquals(List.of("exec", "stop"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("removes the container and recreates it when fresh")
  void recreateWhenFresh(final String runtime) {
    useRuntime(runtime);
    options.fresh = true;
    executor.probeState(ContainerState.RUNNING);
    shell.call();
    assertIterableEquals(List.of("rm", "run", "exec", "stop"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("removes the container and its image instead of keeping them")
  void removeImageTearsDown(final String runtime) {
    useRuntime(runtime);
    options.removeImage = true;
    executor.probeState(ContainerState.ABSENT);
    shell.call();
    assertIterableEquals(List.of("run", "exec", "rm", "rmi"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("reports the exit code of the attached command")
  void reportsAttachExitCode(final String runtime) {
    useRuntime(runtime);
    executor.probeState(ContainerState.RUNNING);
    executor.exitCodes(7);
    final var exitCode = shell.call();
    assertEquals(7, exitCode);
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("does not create the container when the build fails")
  void shortCircuitsOnBuildFailure(final String runtime) {
    useRuntime(runtime);
    options.containerfile = "Dockerfile";
    executor.probeState(ContainerState.ABSENT);
    // The empty pull step consumes the first exit code; the build then fails.
    executor.exitCodes(0, 1);
    final var exitCode = shell.call();
    assertEquals(1, exitCode);
    assertIterableEquals(List.of("build"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("does not try to remove an absent container when fresh")
  void freshAbsentSkipsRemove(final String runtime) {
    useRuntime(runtime);
    options.fresh = true;
    executor.probeState(ContainerState.ABSENT);
    shell.call();
    assertIterableEquals(List.of("run", "exec", "stop"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("recreates the container so a pull takes effect")
  void pullRecreates(final String runtime) {
    useRuntime(runtime);
    options.pull = true;
    executor.probeState(ContainerState.RUNNING);
    shell.call();
    assertIterableEquals(List.of("rm", "pull", "run", "exec", "stop"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("leaves the container running while another session is still attached")
  void otherSessionSkipsStop(final String runtime) {
    useRuntime(runtime);
    // A process beyond the keepalive (PID 1 and its sleep child) means another terminal is attached.
    executor.processesOutput("PID PPID COMMAND\n1 0 sh\n2 1 sleep\n42 0 bash\n");
    executor.probeState(ContainerState.RUNNING);
    shell.call();
    assertIterableEquals(List.of("exec"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("leaves the container running on exit with --keep-running")
  void keepRunningSkipsStop(final String runtime) {
    useRuntime(runtime);
    options.keepRunningOnExit = true;
    // Only the keepalive remains, so this would normally be stopped; --keep-running leaves it up.
    executor.processesOutput("PID PPID COMMAND\n1 0 sh\n2 1 sleep\n");
    executor.probeState(ContainerState.RUNNING);
    shell.call();
    assertIterableEquals(List.of("exec"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("with the keepalive off, uses the inspected main PID to stop when no session remains")
  void noOverrideCommandStopsViaMainPid(final String runtime) {
    useRuntime(runtime);
    options.overrideCommand = false;
    executor.inspectOutput("5000");
    // Host-PID 'top': only the main process (5000) and its worker child — no attached session.
    executor.processesOutput("UID PID PPID COMMAND\nroot 5000 4990 init\nroot 5001 5000 worker\n");
    executor.probeState(ContainerState.RUNNING);
    shell.call();
    assertIterableEquals(List.of("exec", "stop"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("with the keepalive off, keeps running when a session sits beside the main process")
  void noOverrideCommandKeepsRunningWithSession(final String runtime) {
    useRuntime(runtime);
    options.overrideCommand = false;
    executor.inspectOutput("5000");
    executor.processesOutput("UID PID PPID COMMAND\nroot 5000 4990 init\nroot 5050 4990 bash\n");
    executor.probeState(ContainerState.RUNNING);
    shell.call();
    assertIterableEquals(List.of("exec"), operations());
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("stops the container when this is the last attached session")
  void lastSessionStops(final String runtime) {
    useRuntime(runtime);
    // Only the keepalive remains, so this is the last session out and the container is stopped.
    executor.processesOutput("PID PPID COMMAND\n1 0 sh\n2 1 sleep\n");
    executor.probeState(ContainerState.RUNNING);
    shell.call();
    assertIterableEquals(List.of("exec", "stop"), operations());
  }

  @Test
  @DisplayName("removes stale project containers but keeps the current one")
  void sweepsStaleContainers() {
    options.runtime = ShellRuntime.DOCKER;
    final var keep = ShellContainer.name(options, System.getProperty("user.dir"));
    executor.captureOutput(keep + "\nilo-other-111111111111\n");
    executor.probeState(ContainerState.RUNNING);
    shell.call();
    // The current container is running, so it is attached to and then stopped; the unrelated stale
    // container is removed, while the current one is never an 'rm' target.
    assertIterableEquals(List.of("rm", "exec", "stop"), operations());
    assertTrue(executor.executed().stream().anyMatch(command -> command.contains("ilo-other-111111111111")));
    assertTrue(executor.executed().stream()
        .noneMatch(command -> "rm".equals(command.get(1)) && command.contains(keep)));
  }

  private void useRuntime(final String runtime) {
    options.runtime = ShellRuntime.fromAlias(runtime);
  }

}
