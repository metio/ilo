/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.errors;

public final class NoMatchingRuntimeException extends BusinessException {

  public NoMatchingRuntimeException() {
    super(107, null, "No matching runtime was found on your system. Select another runtime using '--runtime' or install your preferred runtime on your system.");
  }

}
