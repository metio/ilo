/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SessionLifecycle")
class SessionLifecycleTest {

  // Each step carries a single marker token so the executed sequence is easy to assert.
  private static final SessionLifecycle.Steps STEPS = new SessionLifecycle.Steps(
      List.of("probe"),
      List.of("remove"),
      List.of("pull"),
      List.of("build"),
      List.of("create"),
      List.of("start"),
      List.of("attach"),
      List.of(List.of("stop")));

  // Records the first token of every non-empty command line and returns configurable exit codes.
  private static final class RecordingExecutor implements SessionLifecycle.Executor {
    final List<String> executed = new ArrayList<>();
    final Map<String, Integer> failures = new HashMap<>();

    @Override
    public int execute(final List<String> arguments, final boolean debug) {
      if (arguments.isEmpty()) {
        return 0;
      }
      final var marker = arguments.get(0);
      executed.add(marker);
      return failures.getOrDefault(marker, 0);
    }
  }

  private static SessionLifecycle.Probe probeReturning(final ContainerState state) {
    return _ -> state;
  }

  @Test
  @DisplayName("creates and prepares an absent container")
  void shouldCreateWhenAbsent() {
    final var executor = new RecordingExecutor();
    final var exitCode = SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(),
        false, false, executor, probeReturning(ContainerState.ABSENT));
    assertEquals(0, exitCode);
    assertIterableEquals(List.of("pull", "build", "create", "attach", "stop"), executor.executed);
  }

  @Test
  @DisplayName("starts a stopped container without rebuilding")
  void shouldStartWhenStopped() {
    final var executor = new RecordingExecutor();
    SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(),
        false, false, executor, probeReturning(ContainerState.STOPPED));
    assertIterableEquals(List.of("start", "attach", "stop"), executor.executed);
  }

  @Test
  @DisplayName("attaches directly to a running container")
  void shouldAttachWhenRunning() {
    final var executor = new RecordingExecutor();
    SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(),
        false, false, executor, probeReturning(ContainerState.RUNNING));
    assertIterableEquals(List.of("attach", "stop"), executor.executed);
  }

  @Test
  @DisplayName("removes the container first and recreates it when fresh")
  void shouldRecreateWhenFresh() {
    final var executor = new RecordingExecutor();
    // A probe that would report RUNNING must be ignored when fresh is requested.
    SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(),
        true, false, executor, probeReturning(ContainerState.RUNNING));
    assertIterableEquals(List.of("remove", "pull", "build", "create", "attach", "stop"), executor.executed);
  }

  @Test
  @DisplayName("does not probe when fresh is requested")
  void shouldSkipProbeWhenFresh() {
    final var executor = new RecordingExecutor();
    final var probed = new ArrayList<List<String>>();
    SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(), true, false, executor, arguments -> {
      probed.add(arguments);
      return ContainerState.RUNNING;
    });
    assertTrue(probed.isEmpty());
  }

  @Test
  @DisplayName("runs create-, start- and attach-time lifecycle commands in order on creation")
  void shouldRunLifecycleInOrderOnCreate() {
    final var executor = new RecordingExecutor();
    final var lifecycle = new SessionLifecycle.Lifecycle(
        List.of(List.of("onCreate")),
        List.of(List.of("onStart")),
        List.of(List.of("onAttach")));
    SessionLifecycle.run(STEPS, lifecycle, false, false, executor, probeReturning(ContainerState.ABSENT));
    assertIterableEquals(
        List.of("pull", "build", "create", "onCreate", "onStart", "onAttach", "attach", "stop"),
        executor.executed);
  }

  @Test
  @DisplayName("skips create-time lifecycle commands when starting a stopped container")
  void shouldSkipCreateLifecycleWhenStopped() {
    final var executor = new RecordingExecutor();
    final var lifecycle = new SessionLifecycle.Lifecycle(
        List.of(List.of("onCreate")),
        List.of(List.of("onStart")),
        List.of(List.of("onAttach")));
    SessionLifecycle.run(STEPS, lifecycle, false, false, executor, probeReturning(ContainerState.STOPPED));
    assertIterableEquals(List.of("start", "onStart", "onAttach", "attach", "stop"), executor.executed);
    assertFalse(executor.executed.contains("onCreate"));
  }

  @Test
  @DisplayName("runs only attach-time lifecycle commands when already running")
  void shouldRunOnlyAttachLifecycleWhenRunning() {
    final var executor = new RecordingExecutor();
    final var lifecycle = new SessionLifecycle.Lifecycle(
        List.of(List.of("onCreate")),
        List.of(List.of("onStart")),
        List.of(List.of("onAttach")));
    SessionLifecycle.run(STEPS, lifecycle, false, false, executor, probeReturning(ContainerState.RUNNING));
    assertIterableEquals(List.of("onAttach", "attach", "stop"), executor.executed);
  }

  @Test
  @DisplayName("short-circuits and reports the exit code when a preparation step fails")
  void shouldShortCircuitOnBuildFailure() {
    final var executor = new RecordingExecutor();
    executor.failures.put("build", 3);
    final var exitCode = SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(),
        false, false, executor, probeReturning(ContainerState.ABSENT));
    assertEquals(3, exitCode);
    assertIterableEquals(List.of("pull", "build"), executor.executed);
  }

  @Test
  @DisplayName("does not attach when a create-time lifecycle command fails")
  void shouldShortCircuitOnLifecycleFailure() {
    final var executor = new RecordingExecutor();
    executor.failures.put("onCreate", 5);
    final var lifecycle = new SessionLifecycle.Lifecycle(
        List.of(List.of("onCreate")), List.of(), List.of());
    final var exitCode = SessionLifecycle.run(STEPS, lifecycle,
        false, false, executor, probeReturning(ContainerState.ABSENT));
    assertEquals(5, exitCode);
    assertFalse(executor.executed.contains("attach"));
  }

  @Test
  @DisplayName("stops the container after the attach returns, even when the attach fails")
  void shouldStopAfterAttachFailure() {
    final var executor = new RecordingExecutor();
    executor.failures.put("attach", 130);
    final var exitCode = SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(),
        false, false, executor, probeReturning(ContainerState.RUNNING));
    assertEquals(130, exitCode);
    assertTrue(executor.executed.contains("stop"));
  }

  @Test
  @DisplayName("runs every teardown command best-effort after the attach")
  void shouldRunAllTeardownCommands() {
    final var executor = new RecordingExecutor();
    executor.failures.put("stop", 1);
    final var steps = new SessionLifecycle.Steps(
        List.of("probe"), List.of("remove"), List.of("pull"), List.of("build"),
        List.of("create"), List.of("start"), List.of("attach"),
        List.of(List.of("stop"), List.of("rmi")));
    final var exitCode = SessionLifecycle.run(steps, SessionLifecycle.Lifecycle.none(),
        false, false, executor, probeReturning(ContainerState.RUNNING));
    assertEquals(0, exitCode);
    assertTrue(executor.executed.contains("stop"));
    assertTrue(executor.executed.contains("rmi"));
  }

  @Test
  @DisplayName("passes the probe command line to the probe")
  void shouldProbeWithProbeArguments() {
    final var executor = new RecordingExecutor();
    final var probed = new ArrayList<List<String>>();
    SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(), false, false, executor, arguments -> {
      probed.add(arguments);
      return ContainerState.RUNNING;
    });
    assertIterableEquals(List.of(List.of("probe")), probed);
  }

}
