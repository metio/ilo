/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.cli;

import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Trust gate for auto-discovered run command files.
 *
 * <p>A trusted file passes immediately. An untrusted file is presented to the user on an interactive
 * terminal; if the user grants trust it is remembered and loaded, otherwise it is skipped. In a
 * non-interactive session (no console, e.g. CI) an untrusted file is refused, so ilo never silently
 * runs commands from a file the user has not vetted.</p>
 */
public final class RcTrustGate implements Predicate<Path> {

  /** Asks the user whether to trust a run command file. */
  @FunctionalInterface
  interface TrustPrompt {

    /**
     * @param runCommandFile The file in question.
     * @param contentChanged Whether this path was trusted before at different content.
     * @return Whether the user grants trust.
     */
    boolean confirm(Path runCommandFile, boolean contentChanged);
  }

  private final Path store;
  private final TrustPrompt prompt;

  public RcTrustGate() {
    this(RcTrust.trustStore(), RcTrustGate::askOnConsole);
  }

  // visible for testing
  RcTrustGate(final Path store, final TrustPrompt prompt) {
    this.store = store;
    this.prompt = prompt;
  }

  @Override
  public boolean test(final Path runCommandFile) {
    if (RcTrust.isTrusted(store, runCommandFile)) {
      return true;
    }
    final var contentChanged = RcTrust.knowsPath(store, runCommandFile);
    if (prompt.confirm(runCommandFile, contentChanged)) {
      RcTrust.trust(store, runCommandFile);
      return true;
    }
    System.err.println("ilo: ignoring untrusted run command file " + runCommandFile.toAbsolutePath());
    return false;
  }

  // visible for testing
  static boolean grants(final String answer) {
    if (null == answer) {
      return false;
    }
    final var normalized = answer.strip().toLowerCase(Locale.ROOT);
    return "y".equals(normalized) || "yes".equals(normalized)
        || "a".equals(normalized) || "always".equals(normalized);
  }

  /**
   * Builds the warning shown before the trust prompt. A changed file is called out separately so the
   * user understands why a file they trusted before is being presented again.
   *
   * @param runCommandFile The file in question.
   * @param contentChanged Whether this path was trusted before at different content.
   * @return The multi-line warning to print.
   */
  // visible for testing
  static String notice(final Path runCommandFile, final boolean contentChanged) {
    final var path = runCommandFile.toAbsolutePath();
    if (contentChanged) {
      return "ilo: the run command file " + path + " has changed since you trusted it."
          + System.lineSeparator()
          + "Re-trusting it lets the new content run arbitrary commands on your machine.";
    }
    return "ilo found an untrusted run command file: " + path
        + System.lineSeparator()
        + "Loading it lets it run arbitrary commands on your machine.";
  }

  // visible for testing
  static boolean askOnConsole(final Path runCommandFile, final boolean contentChanged) {
    if (!Terminal.isInteractive()) {
      System.err.println("ilo: refusing to load untrusted run command file " + runCommandFile.toAbsolutePath()
          + " in a non-interactive session. Run ilo from a terminal once to trust it, or remove the file.");
      return false;
    }
    System.err.println(notice(runCommandFile, contentChanged));
    return grants(System.console().readLine("Trust and load this file from now on? [y/N] "));
  }

}
