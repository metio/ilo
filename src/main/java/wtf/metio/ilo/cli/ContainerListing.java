/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Interprets the JSON output of Apple's {@code container list --format json}. That runtime has no
 * {@code --filter} and emits no Go-template format, so it cannot narrow the listing to one container
 * or a project the way {@code docker ps --filter … --format '{{…}}'} does; the state probe and the
 * stale-container sweep therefore list every container as JSON and select the relevant entries here.
 *
 * <p>An unreadable listing is treated as empty (no such container / nothing to sweep) rather than
 * fatal: a probe that cannot read the listing reports {@link ContainerState#ABSENT}, and the create
 * path then surfaces any real problem (e.g. a name clash) through the runtime itself.</p>
 */
public final class ContainerListing {

  private static final JsonMapper JSON = JsonMapper.builder().build();

  // The status string Apple's runtime reports for a live container; every other value (stopped,
  // stopping, unknown) is treated as a non-running, startable container.
  private static final String RUNNING = "running";

  /**
   * Reports the state of the named container.
   *
   * @param json          The captured {@code container list --format json} output.
   * @param containerName The reused name of the session's container.
   * @return The state of the matching entry, or {@link ContainerState#ABSENT} when none matches.
   */
  public static ContainerState stateOf(final String json, final String containerName) {
    for (final var entry : entries(json)) {
      if (containerName.equals(nameOf(entry))) {
        return RUNNING.equalsIgnoreCase(statusOf(entry)) ? ContainerState.RUNNING : ContainerState.STOPPED;
      }
    }
    return ContainerState.ABSENT;
  }

  /**
   * Names the project's non-running containers, matched by the {@code ilo.project} label, so a stale
   * one left over from an earlier configuration can be removed without touching a container that is
   * running for a session elsewhere.
   *
   * @param json       The captured {@code container list --format json} output.
   * @param projectDir The absolute project directory, matched against the {@code ilo.project} label.
   * @return The names of this project's non-running containers.
   */
  public static List<String> staleNames(final String json, final String projectDir) {
    final var names = new ArrayList<String>();
    for (final var entry : entries(json)) {
      if (projectDir.equals(labelOf(entry, "ilo.project")) && !RUNNING.equalsIgnoreCase(statusOf(entry))) {
        final var name = nameOf(entry);
        if (null != name && !name.isBlank()) {
          names.add(name);
        }
      }
    }
    return names;
  }

  private static List<JsonNode> entries(final String json) {
    if (null == json || json.isBlank()) {
      return List.of();
    }
    try {
      final var root = JSON.readTree(json);
      if (!root.isArray()) {
        return List.of();
      }
      final var entries = new ArrayList<JsonNode>();
      root.forEach(entries::add);
      return entries;
    } catch (final JacksonException exception) {
      System.err.println("ilo: could not parse 'container list' output: " + exception.getMessage());
      return List.of();
    }
  }

  // The name ilo passed to '--name'. The exact field path in the listing JSON is not yet verified
  // against a real 'container list --format json'; the likely candidates are probed in order.
  // OPEN: confirm the field path on Apple-silicon/macOS hardware.
  private static String nameOf(final JsonNode entry) {
    final var configuration = entry.path("configuration");
    return firstText(
        configuration.path("id"),
        entry.path("id"),
        configuration.path("name"),
        entry.path("name"));
  }

  // OPEN: confirm 'status' is a top-level string enum (running/stopped/stopping/unknown).
  private static String statusOf(final JsonNode entry) {
    return entry.path("status").stringValue("");
  }

  // OPEN: confirm labels are an object map under 'configuration.labels'.
  private static String labelOf(final JsonNode entry, final String label) {
    return entry.path("configuration").path("labels").path(label).stringValue(null);
  }

  private static String firstText(final JsonNode... nodes) {
    for (final var node : nodes) {
      final var value = node.stringValue(null);
      if (null != value && !value.isBlank()) {
        return value;
      }
    }
    return null;
  }

  private ContainerListing() {
    // utility class
  }

}
