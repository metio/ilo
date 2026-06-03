/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import java.util.List;

/**
 * The keepalive that holds a session container open between attaches. It is injected as both the
 * entrypoint and the command so it runs regardless of the image's own entrypoint — otherwise an
 * image entrypoint would receive the script as arguments and the keepalive would never run.
 *
 * <p>As PID 1 the shell receives no default signal handling, so it traps SIGTERM and {@code wait}s on
 * a backgrounded sleep (which a signal can interrupt) — otherwise {@code stop} would block for the
 * full grace period before the runtime resorts to SIGKILL. {@code sh} and a numeric {@code sleep}
 * exist on practically every image, including BusyBox-based ones. Holding PID 1 to this known process
 * also lets a session be detected from the runtime alone: see {@link ContainerProcesses}.
 */
public final class Keepalive {

  /** The executable run as the container's entrypoint. */
  public static final String ENTRYPOINT = "sh";

  /** The script run by the entrypoint, passed after a {@code -c} flag. */
  public static final String SCRIPT =
      "trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done";

  /** The entrypoint and its script as a single argument list ({@code sh -c "<script>"}). */
  public static List<String> command() {
    return List.of(ENTRYPOINT, "-c", SCRIPT);
  }

  private Keepalive() {
    // utility class
  }

}
