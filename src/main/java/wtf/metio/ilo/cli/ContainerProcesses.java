/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import java.util.List;
import java.util.Locale;

/**
 * Interprets a container runtime's {@code top} output to decide whether a session is attached. The
 * {@code top} is run against one specific container (selected by its fingerprinted name), so the
 * output only ever lists that container's processes — the question is purely "is anything other than
 * the keepalive running in <em>this</em> container", never a comparison across containers or projects.
 *
 * <p>A persistent session container runs a keepalive (a shell and its {@code sleep} child); every
 * attached terminal is an {@code exec}'d process. The keepalive is recognised primarily by its command
 * marker ({@link Keepalive#SLEEP_SECONDS}, which appears in both the keepalive shell's command and its
 * sleep), so it is told apart from a session whatever PIDs the runtime reports — {@code podman top}
 * shows container-namespace PIDs (keepalive at 1), {@code docker top} shows host PIDs (keepalive not at
 * 1). As a fallback (e.g. {@code --no-override-command}, where there is no keepalive) PID 1 and its
 * direct children are treated as infrastructure too. Detection thus needs no host-side bookkeeping.
 */
public final class ContainerProcesses {

  /**
   * Reports whether the {@code top} output shows at least one attached session.
   *
   * @param topOutput The captured standard output of the runtime's {@code top} command. The header
   *                  row names the columns (positions vary by runtime, so they are looked up by name).
   * @return {@code true} if a session process is present, {@code false} for keepalive-only output or
   * output that has no usable header (e.g. an empty capture from a container that is already gone).
   */
  public static boolean hasSessions(final String topOutput) {
    return hasSessions(topOutput, "");
  }

  /**
   * Reports whether the {@code top} output shows at least one attached session.
   *
   * @param topOutput The captured standard output of the runtime's {@code top} command.
   * @param mainPid   The host PID of the container's main process (from inspect), used to recognise it
   *                  when there is no keepalive marker and {@code top} reports host PIDs (Docker); blank
   *                  when the keepalive's marker is relied on instead.
   * @return {@code true} if a session process is present.
   */
  public static boolean hasSessions(final String topOutput, final String mainPid) {
    final var lines = topOutput.lines().toList();
    // The header is located by name, not position, so 'docker top' (header first) and 'compose top'
    // (a container-name line before the header) are both handled.
    final var headerIndex = headerIndex(lines);
    if (headerIndex < 0) {
      return false;
    }
    final var header = lines.get(headerIndex).trim().split("\\s+");
    final var pid = indexOf(header, "PID");
    final var ppid = indexOf(header, "PPID");
    return lines.stream()
        .skip(headerIndex + 1L)
        .anyMatch(line -> isSession(line, pid, ppid, mainPid));
  }

  // A process line is an attached session unless it belongs to the container's own infrastructure. The
  // keepalive is matched first by its command marker (so it is recognised under host PIDs as well as
  // container PIDs). Otherwise a process is infrastructure when it is the container's main process or a
  // direct child of it — identified as PID 1 (container-namespace 'top', e.g. podman) or as the
  // inspected main host PID (host-namespace 'top', e.g. docker), so both are covered symmetrically.
  private static boolean isSession(final String line, final int pid, final int ppid, final String mainPid) {
    if (line.isBlank() || line.contains(Keepalive.SLEEP_SECONDS)) {
      return false;
    }
    final var columns = line.trim().split("\\s+");
    if (columns.length <= Math.max(pid, ppid)) {
      return false;
    }
    return !isMainProcess(columns[pid], mainPid) && !isMainProcess(columns[ppid], mainPid);
  }

  // The container's main process is PID 1 under a container-namespace 'top', or the inspected main host
  // PID under a host-namespace 'top'.
  private static boolean isMainProcess(final String value, final String mainPid) {
    return "1".equals(value) || (!mainPid.isBlank() && mainPid.equals(value));
  }

  // The first line that names both a PID and a PPID column is the process-table header.
  private static int headerIndex(final List<String> lines) {
    for (var index = 0; index < lines.size(); index++) {
      final var columns = lines.get(index).trim().split("\\s+");
      if (indexOf(columns, "PID") >= 0 && indexOf(columns, "PPID") >= 0) {
        return index;
      }
    }
    return -1;
  }

  private static int indexOf(final String[] header, final String column) {
    for (var index = 0; index < header.length; index++) {
      if (column.equals(header[index].toUpperCase(Locale.ROOT))) {
        return index;
      }
    }
    return -1;
  }

  private ContainerProcesses() {
    // utility class
  }

}
