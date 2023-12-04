/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

public final class NoMatchingRuntimeException extends BusinessException {

  public NoMatchingRuntimeException() {
    super(107, null, "No matching runtime was found on your system. Select another runtime using '--runtime' or install your preferred runtime on your system.");
  }

}
