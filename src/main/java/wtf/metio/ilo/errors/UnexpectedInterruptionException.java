/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

public final class UnexpectedInterruptionException extends BusinessException {

  public UnexpectedInterruptionException(final InterruptedException exception) {
    super(106, exception, "The process was unexpected interrupted. In case you can reproduce this, open a ticket at https://github.com/metio/ilo.");
  }

}
