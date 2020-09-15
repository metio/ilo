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

@DisplayName("CommandListIsEmptyException")
class CommandListIsEmptyExceptionTest {

  @Test
  @DisplayName("has correct exit code and message")
  void exception() {
    final var exception = new CommandListIsEmptyException(new IndexOutOfBoundsException());
    assertAll("exception",
      () -> assertEquals(102, exception.getExitCode(), "exitCode"),
      () -> assertEquals("The generated command list is empty - this is a bug in ilo!", exception.getMessage(), "message"));
  }

}
