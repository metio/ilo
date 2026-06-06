/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ContainerProcesses")
class ContainerProcessesTest {

  // The keepalive: PID 1 is the shell, PID 2 its sleep child (parent 1).
  private static final String KEEPALIVE = """
      USER  PID  PPID  COMMAND
      root  1    0     sh -c trap 'exit 0' TERM INT; while true; do sleep 1 & wait $!; done
      root  2    1     sleep 1
      """;

  @Test
  @DisplayName("reports no session for keepalive-only output")
  void keepaliveOnly() {
    assertFalse(ContainerProcesses.hasSessions(KEEPALIVE));
  }

  @Test
  @DisplayName("detects an attached session as a process that is neither PID 1 nor its child")
  void detectsSession() {
    final var output = KEEPALIVE + "root  37  0  bash\n";
    assertTrue(ContainerProcesses.hasSessions(output));
  }

  @Test
  @DisplayName("finds the PID and PPID columns by name regardless of position")
  void columnsByName() {
    // Docker's default 'ps -ef' layout: UID before PID, CMD last.
    final var output = """
        UID  PID  PPID  C  STIME  TTY  TIME  CMD
        root 1    0     0  10:00  ?    0:00  sh -c ...
        root 2    1     0  10:00  ?    0:00  sleep 1
        root 9    0     0  10:01  ?    0:00  mvn verify
        """;
    assertTrue(ContainerProcesses.hasSessions(output));
  }

  // docker top reports HOST PIDs, so the keepalive is not PID 1 — it is recognised by its command
  // marker (the sleep duration) instead, which appears in both the shell and its sleep child.
  private static final String HOST_PID_KEEPALIVE = """
      UID  PID    PPID   COMMAND
      root 12345  12340  sh -c trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done
      root 12346  12345  sleep 2147483647
      """;

  @Test
  @DisplayName("recognises a host-PID keepalive by its command marker (docker top)")
  void hostPidKeepaliveOnly() {
    assertFalse(ContainerProcesses.hasSessions(HOST_PID_KEEPALIVE));
  }

  @Test
  @DisplayName("detects a session alongside a host-PID keepalive (docker top)")
  void hostPidWithSession() {
    assertTrue(ContainerProcesses.hasSessions(HOST_PID_KEEPALIVE + "root 12399 12340 bash\n"));
  }

  // --no-override-command on docker: no keepalive marker and host PIDs, so the container's own main
  // process is identified by its inspected host PID (5000 here) instead of PID 1.
  private static final String HOST_PID_MAIN_PROCESS = """
      UID  PID   PPID  COMMAND
      root 5000  4990  /sbin/init --serve
      root 5001  5000  worker
      """;

  @Test
  @DisplayName("excludes the main process and its children by inspected host PID (docker, no keepalive)")
  void hostPidMainProcessOnly() {
    assertFalse(ContainerProcesses.hasSessions(HOST_PID_MAIN_PROCESS, "5000"));
  }

  @Test
  @DisplayName("detects a session beside the host-PID main process")
  void hostPidMainProcessWithSession() {
    // An exec'd session is a child of the runtime shim (PPID 4990), not of the main process (5000).
    assertTrue(ContainerProcesses.hasSessions(HOST_PID_MAIN_PROCESS + "root 5050 4990 bash\n", "5000"));
  }

  @Test
  @DisplayName("without an inspected main PID the host-PID main process is miscounted as a session")
  void hostPidMainProcessNeedsInspect() {
    // Confirms the inspected PID is what closes the gap: with no marker and host PIDs, the main process
    // is otherwise indistinguishable from a session.
    assertTrue(ContainerProcesses.hasSessions(HOST_PID_MAIN_PROCESS));
  }

  @Test
  @DisplayName("counts a session whose non-command column merely equals the marker value")
  void sessionWithMarkerInNonCommandColumn() {
    // The keepalive marker (its sleep duration) happens to appear in the ELAPSED column of a genuine
    // session whose command is an ordinary shell; only a marker in the command column means keepalive.
    final var output = """
        USER  PID    PPID   ELAPSED     COMMAND
        root  12345  12340  00:00       sh -c trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done
        root  12346  12345  00:00       sleep 2147483647
        root  12399  12340  2147483647  bash
        """;
    assertTrue(ContainerProcesses.hasSessions(output));
  }

  @Test
  @DisplayName("recognises the keepalive by the marker in its command even without a command column")
  void keepaliveMarkerFallbackWithoutCommandColumn() {
    // A 'top' variant that names neither COMMAND nor CMD falls back to matching the whole row.
    final var output = """
        USER  PID    PPID
        root  12345  12340  sleep 2147483647
        """;
    assertFalse(ContainerProcesses.hasSessions(output));
  }

  @Test
  @DisplayName("counts a session whose command merely contains the marker as a substring")
  void sessionWithMarkerSubstringInCommand() {
    // 'worker-2147483647' contains the marker but is not the keepalive's 'sleep 2147483647' token.
    assertTrue(ContainerProcesses.hasSessions(HOST_PID_KEEPALIVE + "root 12399 12340 worker-2147483647\n"));
  }

  @Test
  @DisplayName("treats empty output as no session")
  void emptyOutput() {
    assertFalse(ContainerProcesses.hasSessions(""));
  }

  @Test
  @DisplayName("treats output without a PID/PPID header as no session")
  void noUsableHeader() {
    assertFalse(ContainerProcesses.hasSessions("some unexpected output\nwith no columns\n"));
  }

  @Test
  @DisplayName("a header with no data rows is no session")
  void headerOnly() {
    assertFalse(ContainerProcesses.hasSessions("USER PID PPID COMMAND\n"));
  }

  @Test
  @DisplayName("finds the header after a leading container-name line (compose top format)")
  void composeTopWithSession() {
    final var output = "project-dev-1\nUID PID PPID CMD\nroot 1 0 sh\nroot 2 1 sleep\nroot 42 0 bash\n";
    assertTrue(ContainerProcesses.hasSessions(output));
  }

  @Test
  @DisplayName("compose top showing only the keepalive is no session")
  void composeTopKeepaliveOnly() {
    final var output = "project-dev-1\nUID PID PPID CMD\nroot 1 0 sh\nroot 2 1 sleep\n";
    assertFalse(ContainerProcesses.hasSessions(output));
  }

}
