/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static wtf.metio.ilo.test.TestResources.testResources;

@DisplayName("Ilo")
class IloTest {

  @Test
  @DisplayName("--help return exit code 0")
  void exitCodeForHelp() throws Exception {
    final var exitCode = catchSystemExit(() -> Ilo.main("--help"));
    assertEquals(0, exitCode);
  }

  @Test
  @DisplayName("-h return exit code 0")
  void exitCodeForShortHelp() throws Exception {
    final var exitCode = catchSystemExit(() -> Ilo.main("-h"));
    assertEquals(0, exitCode);
  }

  @Test
  @DisplayName("--version return exit code 0")
  void exitCodeForVersion() throws Exception {
    final var exitCode = catchSystemExit(() -> Ilo.main("--version"));
    assertEquals(0, exitCode);
  }

  @Test
  @DisplayName("-V return exit code 0")
  void exitCodeForShortVersion() throws Exception {
    final var exitCode = catchSystemExit(() -> Ilo.main("-V"));
    assertEquals(0, exitCode);
  }

  @Test
  @DisplayName("reads .rc files")
  void supportsRunCommands() throws Exception {
    restoreSystemProperties(() -> {
      System.setProperty("user.dir", testResources(Ilo.class).resolve("root").toAbsolutePath().toString());
      assertEquals(1, Ilo.runCommands(new String[]{}).count());
    });
  }

  @Test
  @DisplayName("does not need .rc files")
  void runsWithRunCommands() throws Exception {
    restoreSystemProperties(() -> {
      System.setProperty("user.dir", testResources(Ilo.class).resolve("empty").toAbsolutePath().toString());
      assertEquals(0, Ilo.runCommands(new String[]{}).count());
    });
  }

}
