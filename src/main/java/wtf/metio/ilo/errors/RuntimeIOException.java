/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

import java.io.IOException;

public final class RuntimeIOException extends BusinessException {

  public RuntimeIOException(final IOException exception) {
    super(104, exception, "I/O error occurred.");
  }

}
