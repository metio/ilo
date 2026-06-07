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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ContainerListing")
class ContainerListingTest {

  private static String entry(final String name, final String status, final String project) {
    return "{\"status\":\"" + status + "\",\"configuration\":{\"id\":\"" + name
        + "\",\"labels\":{\"ilo.project\":\"" + project + "\"}}}";
  }

  @Test
  @DisplayName("reads a running container's state")
  void stateRunning() {
    final var json = "[" + entry("ilo-a", "running", "/work") + "]";
    assertEquals(ContainerState.RUNNING, ContainerListing.stateOf(json, "ilo-a"));
  }

  @ParameterizedTest
  @DisplayName("treats every non-running status as stopped")
  @ValueSource(strings = {"stopped", "stopping", "unknown"})
  void stateStopped(final String status) {
    final var json = "[" + entry("ilo-a", status, "/work") + "]";
    assertEquals(ContainerState.STOPPED, ContainerListing.stateOf(json, "ilo-a"));
  }

  @Test
  @DisplayName("reports an absent container when no entry matches the name")
  void stateAbsentWhenUnmatched() {
    final var json = "[" + entry("ilo-other", "running", "/work") + "]";
    assertEquals(ContainerState.ABSENT, ContainerListing.stateOf(json, "ilo-a"));
  }

  @ParameterizedTest
  @DisplayName("reports an absent container for blank output")
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "\n"})
  void stateAbsentForBlank(final String json) {
    assertEquals(ContainerState.ABSENT, ContainerListing.stateOf(json, "ilo-a"));
  }

  @Test
  @DisplayName("falls back to the next name field when the first is blank")
  void stateMatchesByFallbackName() {
    // 'configuration.id' is blank, so the match is made on the top-level 'id'.
    final var json = "[{\"id\":\"ilo-a\",\"status\":\"running\",\"configuration\":{\"id\":\"\"}}]";
    assertEquals(ContainerState.RUNNING, ContainerListing.stateOf(json, "ilo-a"));
  }

  @Test
  @DisplayName("treats a non-array listing as empty")
  void stateForNonArray() {
    assertEquals(ContainerState.ABSENT, ContainerListing.stateOf("{}", "ilo-a"));
  }

  @Test
  @DisplayName("treats unparseable output as empty rather than failing")
  void stateForMalformedJson() {
    assertEquals(ContainerState.ABSENT, ContainerListing.stateOf("{not json", "ilo-a"));
  }

  @Test
  @DisplayName("selects this project's non-running containers")
  void staleNamesSelectsStoppedForProject() {
    final var json = "["
        + entry("ilo-old", "stopped", "/work") + ","
        + entry("ilo-live", "running", "/work") + ","
        + entry("ilo-elsewhere", "stopped", "/other")
        + "]";
    assertEquals(List.of("ilo-old"), ContainerListing.staleNames(json, "/work"));
  }

  @Test
  @DisplayName("skips an entry without a usable name")
  void staleNamesSkipsNamelessEntry() {
    final var json = "[{\"status\":\"stopped\",\"configuration\":{\"labels\":{\"ilo.project\":\"/work\"}}}]";
    assertTrue(ContainerListing.staleNames(json, "/work").isEmpty());
  }

  @Test
  @DisplayName("skips an entry without the project label")
  void staleNamesSkipsUnlabelled() {
    final var json = "[{\"status\":\"stopped\",\"configuration\":{\"id\":\"ilo-a\"}}]";
    assertTrue(ContainerListing.staleNames(json, "/work").isEmpty());
  }

  @Test
  @DisplayName("treats unparseable output as nothing to sweep")
  void staleNamesForMalformedJson() {
    assertTrue(ContainerListing.staleNames("oops", "/work").isEmpty());
  }

}
