/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.errors;

import picocli.CommandLine;

public class ExitCodes implements CommandLine.IExitCodeExceptionMapper {

  @Override
  public int getExitCode(final Throwable exception) {
    if (exception instanceof CommandListContainsNullException) {
      return ((CommandListContainsNullException) exception).getExitCode();
    }
    if (exception instanceof CommandListIsEmptyException) {
      return ((CommandListIsEmptyException) exception).getExitCode();
    }
    if (exception instanceof OperatingSystemNotSupportedException) {
      return ((OperatingSystemNotSupportedException) exception).getExitCode();
    }
    if (exception instanceof RuntimeIOException) {
      return ((RuntimeIOException) exception).getExitCode();
    }
    if (exception instanceof SecurityManagerDeniesAccessException) {
      return ((SecurityManagerDeniesAccessException) exception).getExitCode();
    }
    return CommandLine.ExitCode.SOFTWARE;
  }

}
