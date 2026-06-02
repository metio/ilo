/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.cli;

import java.util.Locale;

/**
 * The lifecycle state of a managed container, as observed before a session starts. The session
 * branches on this: an absent container is created, a stopped one is started, and a running one is
 * attached to directly.
 */
public enum ContainerState {

  /** No container with the session's name exists yet. */
  ABSENT,

  /** A container exists but is not running (e.g. created, exited, or stopped). */
  STOPPED,

  /** A container exists and is currently running. */
  RUNNING;

  /**
   * Interprets the output of a {@code ps --format '{{.State}}'} probe. The probe filters on the exact
   * container name, so at most one line is expected; a blank result means no such container exists.
   *
   * @param probeOutput The trimmed standard output of the state probe.
   * @return The observed state.
   */
  public static ContainerState fromProbe(final String probeOutput) {
    if (null == probeOutput || probeOutput.isBlank()) {
      return ABSENT;
    }
    final var firstLine = probeOutput.strip().lines().findFirst().orElse("").strip();
    return "running".equals(firstLine.toLowerCase(Locale.ROOT)) ? RUNNING : STOPPED;
  }

}
