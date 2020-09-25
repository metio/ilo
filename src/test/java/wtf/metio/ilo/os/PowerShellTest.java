/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.os;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.*;
import static wtf.metio.ilo.os.ParameterExpansion.MATCHER_GROUP_NAME;

@DisplayName("PowerShell")
class PowerShellTest {

  @Nested
  @DisplayName("regex")
  class Regex {

    @Test
    @DisplayName("regex for command")
    void regexMatchesCommand() {
      final var matcher = PowerShell.COMMAND_PATTERN.matcher("$(some-command --with-option)");
      assertAll("new style",
        () -> assertTrue(matcher.find(), "matches"),
        () -> assertEquals("some-command --with-option", matcher.group(MATCHER_GROUP_NAME), "extraction"));
    }

    @Test
    @DisplayName("regex for commands")
    void regexMatchesCommands() {
      final var matcher = PowerShell.COMMAND_PATTERN.matcher("$(some-command --with-option):$(other --option)");
      assertAll("new style",
        () -> assertTrue(matcher.find(), "first match"),
        () -> assertEquals("some-command --with-option", matcher.group(MATCHER_GROUP_NAME), "first extraction"),
        () -> assertTrue(matcher.find(), "second match"),
        () -> assertEquals("other --option", matcher.group(MATCHER_GROUP_NAME), "second extraction"));
    }

    @Test
    @DisplayName("regex for parameter")
    void regexMatchesParameter() {
      final var matcher = PowerShell.PARAMETER_PATTERN.matcher("$HOME");
      assertAll("new style",
        () -> assertTrue(matcher.find(), "matches"),
        () -> assertEquals("$HOME", matcher.group(MATCHER_GROUP_NAME), "extraction"));
    }

    @Test
    @DisplayName("regex for parameters")
    void regexMatchesParameters() {
      final var matcher = PowerShell.PARAMETER_PATTERN.matcher("$HOME:$OTHER");
      assertAll("new style",
        () -> assertTrue(matcher.find(), "first matches"),
        () -> assertEquals("$HOME", matcher.group(MATCHER_GROUP_NAME), "first extraction"),
        () -> assertTrue(matcher.find(), "second matches"),
        () -> assertEquals("$OTHER", matcher.group(MATCHER_GROUP_NAME), "second extraction"));
    }

  }

  @Nested
  @DisplayName("expansion")
  @EnabledOnOs({OS.WINDOWS})
  class Expansion {

    private ParameterExpansion powerShell;

    @BeforeEach
    void setUp() {
      powerShell = OSSupport.powerShell().orElseThrow();
    }

    @Test
    @DisplayName("replaces parameter")
    void replacesParameter() {
      final var result = powerShell.replace("$HOME:abc", input -> "test", PowerShell.PARAMETER_PATTERN);
      assertEquals("test:abc", result);
    }

    @Test
    @DisplayName("replaces command")
    void replacesCommandWithNewStyle() {
      final var result = powerShell.replace("$(id -u):abc", input -> "test", PowerShell.COMMAND_PATTERN);
      assertEquals("test:abc", result);
    }

    @Test
    @DisplayName("replaces commands")
    void replacesCommandsWithNewStyle() {
      final var result = powerShell.replace("$(id -u):$(id -u)", input -> "test", PowerShell.COMMAND_PATTERN);
      assertEquals("test:test", result);
    }

    @Test
    @DisplayName("keeps constants as-is in parameters")
    void keepConstantsInParameters() {
      assertEquals("1000:1000", powerShell.expandParameters("1000:1000"));
    }

    @Test
    @DisplayName("keeps constants as-is in commands")
    void keepConstantsInCommands() {
      assertEquals("1000:1000", powerShell.substituteCommands("1000:1000"));
    }

    @Test
    @DisplayName("substitutes commands with their results")
    void substituteCommands() {
      assertEquals("hello:world", powerShell.substituteCommands("$(echo hello):$(echo world)"));
    }

    @Test
    @DisplayName("substitutes command with its result and keeps constant")
    void substituteCommandAndKeepConstant() {
      assertEquals("hello:1234", powerShell.substituteCommands("$(echo hello):1234"));
    }

    @Test
    @DisplayName("substitutes command with its result and keeps constant")
    void keepConstantAndSubstituteCommand() {
      assertEquals("1234:world", powerShell.substituteCommands("1234:$(echo world)"));
    }

  }

}
