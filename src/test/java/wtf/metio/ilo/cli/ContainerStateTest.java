/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ContainerState")
class ContainerStateTest {

  @ParameterizedTest
  @DisplayName("treats blank probe output as an absent container")
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\n", "   \n  "})
  void shouldTreatBlankAsAbsent(final String probeOutput) {
    assertEquals(ContainerState.ABSENT, ContainerState.fromProbe(probeOutput));
  }

  @ParameterizedTest
  @DisplayName("treats a running state as running, case-insensitively")
  @ValueSource(strings = {"running", "Running", "RUNNING", " running "})
  void shouldDetectRunning(final String probeOutput) {
    assertEquals(ContainerState.RUNNING, ContainerState.fromProbe(probeOutput));
  }

  @ParameterizedTest
  @DisplayName("treats any other non-blank state as stopped")
  @ValueSource(strings = {"exited", "created", "paused", "dead"})
  void shouldDetectStopped(final String probeOutput) {
    assertEquals(ContainerState.STOPPED, ContainerState.fromProbe(probeOutput));
  }

  @Test
  @DisplayName("interprets only the first line of multi-line output")
  void shouldUseFirstLine() {
    assertEquals(ContainerState.RUNNING, ContainerState.fromProbe("running\nexited"));
  }

}
