/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.errors;

public final class UnexpectedInterruptionException extends BusinessException {

  public UnexpectedInterruptionException(final InterruptedException exception) {
    super(106, exception, "The process was unexpected interrupted. In case you can reproduce this, open a ticket at https://github.com/metio/ilo.");
  }

}
