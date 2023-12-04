/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

import picocli.CommandLine;

public final class ExitCodes implements CommandLine.IExitCodeExceptionMapper {

  @Override
  public int getExitCode(final Throwable exception) {
    if (exception instanceof BusinessException) {
      return ((BusinessException) exception).getExitCode();
    }
    return CommandLine.ExitCode.SOFTWARE;
  }

}
