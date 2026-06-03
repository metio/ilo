/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import java.util.List;
import java.util.Locale;

/**
 * Interprets a container runtime's {@code top} output to decide whether a session is attached. A
 * persistent session container runs a keepalive as PID 1 with its sleep as a direct child; every
 * attached terminal is an {@code exec}'d process, which is neither PID 1 nor parented to it. So a
 * process with both {@code PID != 1} and {@code PPID != 1} is an open session — letting the last
 * session out be detected from the runtime alone, with no host-side bookkeeping.
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
        .map(line -> line.trim().split("\\s+"))
        .filter(columns -> columns.length > Math.max(pid, ppid))
        .anyMatch(columns -> !"1".equals(columns[pid]) && !"1".equals(columns[ppid]));
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
