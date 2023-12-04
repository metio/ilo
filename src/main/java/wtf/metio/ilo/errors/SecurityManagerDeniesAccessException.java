/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

public final class SecurityManagerDeniesAccessException extends BusinessException {

  public SecurityManagerDeniesAccessException(final SecurityException exception) {
    super(105, exception, "A Java SecurityManager does not allow creating new processes.");
  }

}
