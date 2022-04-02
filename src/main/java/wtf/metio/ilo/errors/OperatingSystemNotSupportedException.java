/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.errors;

public final class OperatingSystemNotSupportedException extends BusinessException {

  public OperatingSystemNotSupportedException(final UnsupportedOperationException exception) {
    super(103, exception, "Your operating system does not support the creation of processes - sadly ilo won't work here.");
  }

}
