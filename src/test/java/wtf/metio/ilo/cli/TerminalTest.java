/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Terminal")
class TerminalTest {

  @Test
  @DisplayName("treats a missing console as non-interactive")
  void missingConsoleIsNotInteractive() {
    assertFalse(Terminal.isInteractive((java.io.Console) null));
  }

  @Test
  @DisplayName("is not interactive in a forked, non-terminal test session")
  void testSessionIsNotInteractive() {
    // Surefire forks the JVM with redirected streams, so no real terminal is attached.
    assertFalse(Terminal.isInteractive());
  }

}
