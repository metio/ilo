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
