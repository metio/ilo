/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemOut;
import wtf.metio.ilo.errors.RuntimeIOException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SessionLifecycle")
@ExtendWith(SystemStubsExtension.class)
class SessionLifecycleTest {

  // Each step carries a single marker token so the executed sequence is easy to assert.
  private static final SessionLifecycle.Steps STEPS = new SessionLifecycle.Steps(
      List.of("remove"),
      List.of("pull"),
      List.of("build"),
      List.of("create"),
      List.of("start"),
      List.of("attach"),
      () -> List.of(List.of("stop")));

  // Records the first token of every non-empty command line and returns configurable exit codes.
  private static final class RecordingExecutor implements SessionLifecycle.Executor {
    final List<String> executed = new ArrayList<>();
    final Map<String, Integer> failures = new HashMap<>();

    @Override
    public synchronized int execute(final List<String> arguments, final boolean debug) {
      if (arguments.isEmpty()) {
        return 0;
      }
      final var marker = arguments.get(0);
      executed.add(marker);
      return failures.getOrDefault(marker, 0);
    }
  }

  // A single-command lifecycle step carrying one marker token.
  private static List<List<String>> step(final String marker) {
    return List.of(List.of(marker));
  }

  @Test
  @DisplayName("creates and prepares an absent container")
  void shouldCreateWhenAbsent() {
    final var executor = new RecordingExecutor();
    final var exitCode = SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(),
        false, false, executor, ContainerState.ABSENT);
    assertEquals(0, exitCode);
    assertIterableEquals(List.of("pull", "build", "create", "attach", "stop"), executor.executed);
  }

  @Test
  @DisplayName("starts a stopped container without rebuilding")
  void shouldStartWhenStopped() {
    final var executor = new RecordingExecutor();
    SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(),
        false, false, executor, ContainerState.STOPPED);
    assertIterableEquals(List.of("start", "attach", "stop"), executor.executed);
  }

  @Test
  @DisplayName("attaches directly to a running container")
  void shouldAttachWhenRunning() {
    final var executor = new RecordingExecutor();
    SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(),
        false, false, executor, ContainerState.RUNNING);
    assertIterableEquals(List.of("attach", "stop"), executor.executed);
  }

  @Test
  @DisplayName("removes the container first and recreates it when fresh")
  void shouldRecreateWhenFresh() {
    final var executor = new RecordingExecutor();
    // A state of RUNNING must be ignored when fresh is requested.
    SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(),
        true, false, executor, ContainerState.RUNNING);
    assertIterableEquals(List.of("remove", "pull", "build", "create", "attach", "stop"), executor.executed);
  }

  @Test
  @DisplayName("runs create-, start- and attach-time lifecycle commands in order on creation")
  void shouldRunLifecycleInOrderOnCreate() {
    final var executor = new RecordingExecutor();
    final var lifecycle = new SessionLifecycle.Lifecycle(
        List.of(step("onCreate")),
        List.of(step("onStart")),
        List.of(step("onAttach")));
    SessionLifecycle.run(STEPS, lifecycle, false, false, executor, ContainerState.ABSENT);
    assertIterableEquals(
        List.of("pull", "build", "create", "onCreate", "onStart", "onAttach", "attach", "stop"),
        executor.executed);
  }

  @Test
  @DisplayName("skips create-time lifecycle commands when starting a stopped container")
  void shouldSkipCreateLifecycleWhenStopped() {
    final var executor = new RecordingExecutor();
    final var lifecycle = new SessionLifecycle.Lifecycle(
        List.of(step("onCreate")),
        List.of(step("onStart")),
        List.of(step("onAttach")));
    SessionLifecycle.run(STEPS, lifecycle, false, false, executor, ContainerState.STOPPED);
    assertIterableEquals(List.of("start", "onStart", "onAttach", "attach", "stop"), executor.executed);
    assertFalse(executor.executed.contains("onCreate"));
  }

  @Test
  @DisplayName("runs only attach-time lifecycle commands when already running")
  void shouldRunOnlyAttachLifecycleWhenRunning() {
    final var executor = new RecordingExecutor();
    final var lifecycle = new SessionLifecycle.Lifecycle(
        List.of(step("onCreate")),
        List.of(step("onStart")),
        List.of(step("onAttach")));
    SessionLifecycle.run(STEPS, lifecycle, false, false, executor, ContainerState.RUNNING);
    assertIterableEquals(List.of("onAttach", "attach", "stop"), executor.executed);
  }

  @Test
  @DisplayName("runs the commands of a parallel step and awaits them all")
  void shouldRunParallelStepCommands() {
    final var executor = new RecordingExecutor();
    final var lifecycle = new SessionLifecycle.Lifecycle(
        List.of(List.of(List.of("a"), List.of("b"))), List.of(), List.of());
    final var exitCode = SessionLifecycle.run(STEPS, lifecycle, false, false, executor, ContainerState.ABSENT);
    assertEquals(0, exitCode);
    assertTrue(executor.executed.containsAll(List.of("a", "b")), executor.executed.toString());
    assertTrue(executor.executed.contains("attach"));
  }

  @Test
  @DisplayName("prints each parallel command's captured output as a whole block")
  void shouldPrintCapturedParallelOutput(final SystemOut systemOut) {
    final var executor = new SessionLifecycle.Executor() {
      @Override
      public int execute(final List<String> arguments, final boolean debug) {
        return 0;
      }

      @Override
      public SessionLifecycle.CommandResult executeCaptured(final List<String> arguments, final boolean debug) {
        return new SessionLifecycle.CommandResult(0, "output-of-" + arguments.get(0) + "\n");
      }
    };
    final var lifecycle = new SessionLifecycle.Lifecycle(
        List.of(List.of(List.of("a"), List.of("b"))), List.of(), List.of());
    SessionLifecycle.run(STEPS, lifecycle, false, false, executor, ContainerState.ABSENT);
    assertTrue(systemOut.getText().contains("output-of-a"), systemOut.getText());
    assertTrue(systemOut.getText().contains("output-of-b"), systemOut.getText());
  }

  @Test
  @DisplayName("propagates a parallel command's business exception unwrapped, not as a CompletionException")
  void shouldUnwrapBusinessExceptionFromParallelStep() {
    final SessionLifecycle.Executor executor = (arguments, debug) -> {
      if (arguments.contains("a") || arguments.contains("b")) {
        throw new RuntimeIOException(new IOException("boom"));
      }
      return 0;
    };
    final var lifecycle = new SessionLifecycle.Lifecycle(
        List.of(List.of(List.of("a"), List.of("b"))), List.of(), List.of());
    assertThrows(RuntimeIOException.class,
        () -> SessionLifecycle.run(STEPS, lifecycle, false, false, executor, ContainerState.ABSENT));
  }

  @Test
  @DisplayName("fails the step and skips the attach when a parallel command fails")
  void shouldFailWhenAParallelCommandFails() {
    final var executor = new RecordingExecutor();
    executor.failures.put("b", 9);
    final var lifecycle = new SessionLifecycle.Lifecycle(
        List.of(List.of(List.of("a"), List.of("b"))), List.of(), List.of());
    final var exitCode = SessionLifecycle.run(STEPS, lifecycle, false, false, executor, ContainerState.ABSENT);
    assertEquals(9, exitCode);
    assertFalse(executor.executed.contains("attach"));
  }

  @Test
  @DisplayName("short-circuits and reports the exit code when a preparation step fails")
  void shouldShortCircuitOnBuildFailure() {
    final var executor = new RecordingExecutor();
    executor.failures.put("build", 3);
    final var exitCode = SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(),
        false, false, executor, ContainerState.ABSENT);
    assertEquals(3, exitCode);
    assertIterableEquals(List.of("pull", "build"), executor.executed);
  }

  @Test
  @DisplayName("does not attach when a create-time lifecycle command fails")
  void shouldShortCircuitOnLifecycleFailure() {
    final var executor = new RecordingExecutor();
    executor.failures.put("onCreate", 5);
    final var lifecycle = new SessionLifecycle.Lifecycle(
        List.of(step("onCreate")), List.of(), List.of());
    final var exitCode = SessionLifecycle.run(STEPS, lifecycle,
        false, false, executor, ContainerState.ABSENT);
    assertEquals(5, exitCode);
    assertFalse(executor.executed.contains("attach"));
  }

  @Test
  @DisplayName("does not attach when an attach-time lifecycle command fails")
  void shouldShortCircuitOnAttachLifecycleFailure() {
    final var executor = new RecordingExecutor();
    executor.failures.put("onAttach", 4);
    final var lifecycle = new SessionLifecycle.Lifecycle(List.of(), List.of(), List.of(step("onAttach")));
    final var exitCode = SessionLifecycle.run(STEPS, lifecycle, false, false, executor, ContainerState.RUNNING);
    assertEquals(4, exitCode);
    assertFalse(executor.executed.contains("attach"));
  }

  @Test
  @DisplayName("stops the container after the attach returns, even when the attach fails")
  void shouldStopAfterAttachFailure() {
    final var executor = new RecordingExecutor();
    executor.failures.put("attach", 130);
    final var exitCode = SessionLifecycle.run(STEPS, SessionLifecycle.Lifecycle.none(),
        false, false, executor, ContainerState.RUNNING);
    assertEquals(130, exitCode);
    assertTrue(executor.executed.contains("stop"));
  }

  @Test
  @DisplayName("runs every teardown command best-effort after the attach")
  void shouldRunAllTeardownCommands() {
    final var executor = new RecordingExecutor();
    executor.failures.put("stop", 1);
    final var steps = new SessionLifecycle.Steps(
        List.of("remove"), List.of("pull"), List.of("build"),
        List.of("create"), List.of("start"), List.of("attach"),
        () -> List.of(List.of("stop"), List.of("rmi")));
    final var exitCode = SessionLifecycle.run(steps, SessionLifecycle.Lifecycle.none(),
        false, false, executor, ContainerState.RUNNING);
    assertEquals(0, exitCode);
    assertTrue(executor.executed.contains("stop"));
    assertTrue(executor.executed.contains("rmi"));
  }

}
