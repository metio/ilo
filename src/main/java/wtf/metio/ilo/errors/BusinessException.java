/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

abstract class BusinessException extends RuntimeException {

  private final int exitCode;

  public BusinessException(final int exitCode, final String message) {
    super(message);
    this.exitCode = exitCode;
  }

  public BusinessException(final int exitCode, final Throwable cause, final String message) {
    super(message, cause);
    this.exitCode = exitCode;
  }

  public final int getExitCode() {
    return exitCode;
  }

}
