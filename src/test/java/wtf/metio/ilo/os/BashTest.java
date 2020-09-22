/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.os;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bash")
class BashTest {

  private Bash bash;

  @BeforeEach
  void setUp() {
    bash = new Bash();
  }

  @Test
  @DisplayName("expands ~ to the user's home directory")
  void expandHomeWithTilde() throws Exception {
    SystemLambda.restoreSystemProperties(() -> {
      System.setProperty("user.home", "/home/user");
      final var result = bash.expandParameters("~/test:/something");
      assertEquals("/home/user/test:/something", result);
    });
  }

  @Test
  @DisplayName("expands $HOME to the user's home directory")
  void expandHome() throws Exception {
    SystemLambda.restoreSystemProperties(() -> {
      System.setProperty("user.home", "/home/user");
      final var result = bash.expandParameters("$HOME/test:/something");
      assertEquals("/home/user/test:/something", result);
    });
  }

  @Test
  @DisplayName("expands ${HOME} to the user's home directory")
  void expandHomesWithBrackets() throws Exception {
    final var values = List.of("${HOME}/test:/something", "");
    SystemLambda.restoreSystemProperties(() -> {
      System.setProperty("user.home", "/home/user");
      final var result = Bash.expand(values);
      assertIterableEquals(List.of("/home/user/test:/something"), result);
    });
  }

  @Test
  @DisplayName("returns constants as-is")
  void keepOther() {
    assertEquals("1000:1000", bash.expandParameters("1000:1000"));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("substitutes commands")
  void substituteCommands() {
    final var result = Bash.expand("$(id -u):$(id -g)");
    assertAll("result",
      () -> assertNotEquals("$(id -u):(id -g)", result, "no evaluation"),
      () -> assertNotEquals(":", result, "empty results"),
      () -> assertFalse(result.isBlank(), "not empty"),
      () -> assertFalse(result.contains("id -u"), "user"),
      () -> assertFalse(result.contains("id -g"), "group"));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("substitutes commands and keeps constants")
  void substituteCommandsWithConstant() {
    final var result = Bash.expand("$(id -u):1234");
    assertAll("result",
      () -> assertNotEquals("$(id -u):1234", result, "no evaluation"),
      () -> assertNotEquals(":1234", result, "empty result"),
      () -> assertFalse(result.isBlank(), "not empty"),
      () -> assertFalse(result.contains("id -u"), "user"),
      () -> assertTrue(result.contains("1234"), "group"));
  }

}
