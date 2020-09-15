/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ExitCodes")
class ExitCodesTest {

  @Test
  @DisplayName("handles business exceptions")
  void businessException() {
    final var exitCodes = new ExitCodes();
    final var exception = new NoMatchingRuntimeException();

    final var exitCode = exitCodes.getExitCode(exception);

    assertEquals(exception.getExitCode(), exitCode);
  }

  @Test
  @DisplayName("handles generic exceptions")
  void genericException() {
    final var exitCodes = new ExitCodes();
    final var exception = new RuntimeException();

    final var exitCode = exitCodes.getExitCode(exception);

    assertEquals(CommandLine.ExitCode.SOFTWARE, exitCode);
  }

}
