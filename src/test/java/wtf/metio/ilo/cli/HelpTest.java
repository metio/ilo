/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HelpTest extends CLI_TCK {

  @Test
  void shouldHaveUsageHelp() {
    final var exitCode = cmd.execute("-h");
    Assertions.assertEquals(0, exitCode);
    Assertions.assertTrue(output.toString().startsWith("Usage"));
  }

  @Test
  void shouldHaveHelpForShell() {
    final var exitCode = cmd.execute("shell", "-h");
    Assertions.assertEquals(0, exitCode);
    Assertions.assertTrue(output.toString().startsWith("Usage"));
  }

  @Test
  void shouldHaveHelpForCompose() {
    final var exitCode = cmd.execute("compose", "-h");
    Assertions.assertEquals(0, exitCode);
    Assertions.assertTrue(output.toString().startsWith("Usage"));
  }

}
