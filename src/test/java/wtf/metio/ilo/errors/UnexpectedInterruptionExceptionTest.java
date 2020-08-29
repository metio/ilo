/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.errors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("UnexpectedInterruptionException")
class UnexpectedInterruptionExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var exception = new UnexpectedInterruptionException(new InterruptedException());
    assertAll("exception",
        () -> assertEquals(106, exception.getExitCode(), "exitCode"),
        () -> assertEquals("The process was unexpected interrupted. In case you can reproduce this, open a ticket at https://github.com/metio/ilo.", exception.getMessage(), "message"));
  }

}