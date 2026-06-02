/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

/**
 * Signals that a command ilo ran to read its output (e.g. command substitution while expanding an
 * option value) did not finish within the allotted time and was terminated.
 */
public final class CommandTimedOutException extends BusinessException {

  public CommandTimedOutException(final long seconds, final String command) {
    super(124, "Command did not finish within " + seconds + " seconds and was terminated: " + command);
  }

}
