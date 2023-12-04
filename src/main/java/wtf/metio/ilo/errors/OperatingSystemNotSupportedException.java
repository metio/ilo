/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

public final class OperatingSystemNotSupportedException extends BusinessException {

  public OperatingSystemNotSupportedException(final UnsupportedOperationException exception) {
    super(103, exception, "Your operating system does not support the creation of processes - sadly ilo won't work here.");
  }

}
