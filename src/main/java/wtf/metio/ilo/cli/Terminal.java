/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import java.io.Console;

/**
 * Detects whether ilo is attached to an interactive terminal.
 *
 * <p>{@link System#console()} alone is not enough: since JDK 22 it returns a non-null console even
 * for redirected or piped streams. {@code Console.isTerminal()} distinguishes a real terminal and is
 * called reflectively because ilo is compiled against the Java 21 API. When the answer is uncertain
 * we report non-interactive, so callers default to the safe, non-blocking behavior.</p>
 */
public final class Terminal {

  private Terminal() {
    // utility class
  }

  public static boolean isInteractive() {
    return isInteractive(System.console());
  }

  // visible for testing
  static boolean isInteractive(final Console console) {
    if (null == console) {
      return false;
    }
    try {
      return (boolean) Console.class.getMethod("isTerminal").invoke(console);
    } catch (final NoSuchMethodException unavailableBeforeJava22) {
      return true;
    } catch (final ReflectiveOperationException unexpected) {
      return false;
    }
  }

}
