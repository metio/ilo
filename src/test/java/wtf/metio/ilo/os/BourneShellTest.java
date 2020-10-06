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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.*;
import static wtf.metio.ilo.os.ParameterExpansion.MATCHER_GROUP_NAME;

@DisplayName("Bourne Shell")
class BourneShellTest {

  @Nested
  @DisplayName("regex")
  class Regex {

    @Test
    @DisplayName("regex for command using new style")
    void regexMatchesCommandWithNewStyle() {
      final var matcher = BourneShell.NEW_COMMAND_PATTERN.matcher("$(some-command --with-option)");
      assertAll("new style",
        () -> assertTrue(matcher.find(), "matches"),
        () -> assertEquals("some-command --with-option", matcher.group(MATCHER_GROUP_NAME), "extraction"));
    }

    @Test
    @DisplayName("regex for command using old style")
    void regexMatchesCommandWithOldStyle() {
      final var matcher = BourneShell.OLD_COMMAND_PATTERN.matcher("`some-command --with-option`");
      assertAll("old style",
        () -> assertTrue(matcher.find(), "matches"),
        () -> assertEquals("some-command --with-option", matcher.group(MATCHER_GROUP_NAME), "extraction"));
    }

    @Test
    @DisplayName("regex for commands using new style")
    void regexMatchesCommandsWithNewStyle() {
      final var matcher = BourneShell.NEW_COMMAND_PATTERN.matcher("$(some-command --with-option):$(other --option)");
      assertAll("new style",
        () -> assertTrue(matcher.find(), "first match"),
        () -> assertEquals("some-command --with-option", matcher.group(MATCHER_GROUP_NAME), "first extraction"),
        () -> assertTrue(matcher.find(), "second match"),
        () -> assertEquals("other --option", matcher.group(MATCHER_GROUP_NAME), "second extraction"));
    }

    @Test
    @DisplayName("regex for commands using old style")
    void regexMatchesCommandsWithOldStyle() {
      final var matcher = BourneShell.OLD_COMMAND_PATTERN.matcher("`some-command --with-option`:`other --option`");
      assertAll("old style",
        () -> assertTrue(matcher.find(), "first match"),
        () -> assertEquals("some-command --with-option", matcher.group(MATCHER_GROUP_NAME), "first extraction"),
        () -> assertTrue(matcher.find(), "second match"),
        () -> assertEquals("other --option", matcher.group(MATCHER_GROUP_NAME), "second extraction"));
    }

    @Test
    @DisplayName("regex for parameter")
    void regexMatchesParameter() {
      final var matcher = BourneShell.PARAMETER_PATTERN.matcher("$HOME");
      assertAll("new style",
        () -> assertTrue(matcher.find(), "matches"),
        () -> assertEquals("$HOME", matcher.group(MATCHER_GROUP_NAME), "extraction"));
    }

    @Test
    @DisplayName("regex for parameters")
    void regexMatchesParameters() {
      final var matcher = BourneShell.PARAMETER_PATTERN.matcher("$HOME:$OTHER");
      assertAll("new style",
        () -> assertTrue(matcher.find(), "first matches"),
        () -> assertEquals("$HOME", matcher.group(MATCHER_GROUP_NAME), "first extraction"),
        () -> assertTrue(matcher.find(), "second matches"),
        () -> assertEquals("$OTHER", matcher.group(MATCHER_GROUP_NAME), "second extraction"));
    }

    @Test
    @DisplayName("regex for parameter with braces")
    void regexMatchesParameterWithBraces() {
      final var matcher = BourneShell.PARAMETER_WITH_BRACES_PATTERN.matcher("${HOME}");
      assertAll("new style",
        () -> assertTrue(matcher.find(), "matches"),
        () -> assertEquals("${HOME}", matcher.group(MATCHER_GROUP_NAME), "extraction"));
    }

    @Test
    @DisplayName("regex for parameters with braces")
    void regexMatchesParametersWithBraces() {
      final var matcher = BourneShell.PARAMETER_WITH_BRACES_PATTERN.matcher("${HOME}:${OTHER}");
      assertAll("new style",
        () -> assertTrue(matcher.find(), "matches"),
        () -> assertEquals("${HOME}", matcher.group(MATCHER_GROUP_NAME), "extraction"),
        () -> assertTrue(matcher.find(), "second matches"),
        () -> assertEquals("${OTHER}", matcher.group(MATCHER_GROUP_NAME), "second extraction"));
    }

  }

  @Nested
  @DisplayName("expansion")
  @EnabledOnOs({OS.LINUX, OS.MAC})
  class Expansion {

    private ParameterExpansion bourneShell;

    @BeforeEach
    void setUp() {
      bourneShell = OSSupport.bourneShell().orElseThrow();
    }

    @Test
    @DisplayName("replaces parameter")
    void replacesParameter() {
      final var result = bourneShell.replace("$HOME:abc", input -> "test", BourneShell.PARAMETER_PATTERN);
      assertEquals("test:abc", result);
    }

    @Test
    @DisplayName("replaces parameter with braces")
    void replacesParameterWithBraces() {
      final var result = bourneShell.replace("${HOME}:abc", input -> "test", BourneShell.PARAMETER_WITH_BRACES_PATTERN);
      assertEquals("test:abc", result);
    }

    @Test
    @DisplayName("replaces command with new style")
    void replacesCommandWithNewStyle() {
      final var result = bourneShell.replace("$(id -u):abc", input -> "test", BourneShell.NEW_COMMAND_PATTERN);
      assertEquals("test:abc", result);
    }

    @Test
    @DisplayName("replaces commands with new style")
    void replacesCommandsWithNewStyle() {
      final var result = bourneShell.replace("$(id -u):$(id -u)", input -> "test", BourneShell.NEW_COMMAND_PATTERN);
      assertEquals("test:test", result);
    }

    @Test
    @DisplayName("replaces command with old style")
    void replacesCommandWithOldStyle() {
      final var result = bourneShell.replace("`id -u`:abc", input -> "test", BourneShell.OLD_COMMAND_PATTERN);
      assertEquals("test:abc", result);
    }

    @Test
    @DisplayName("expands ~ to the user's home directory")
    void expandHomeWithTilde() throws Exception {
      SystemLambda.restoreSystemProperties(() -> {
        System.setProperty("user.home", "/home/user");
        final var result = bourneShell.expandParameters("~/test:/something");
        assertEquals("/home/user/test:/something", result);
      });
    }

    @Test
    @DisplayName("keeps constants as-is in parameters")
    void keepConstantsInParameters() {
      assertEquals("1000:1000", bourneShell.expandParameters("1000:1000"));
    }

    @Test
    @DisplayName("keeps constants as-is in commands")
    void keepConstantsInCommands() {
      assertEquals("1000:1000", bourneShell.substituteCommands("1000:1000"));
    }

    @Test
    @DisplayName("substitutes commands with their results")
    void substituteCommands() {
      assertEquals("hello:world", bourneShell.substituteCommands("$(echo hello):$(echo world)"));
    }

    @Test
    @DisplayName("expand parameter with their results")
    void expandParameter() {
      final var homeDirectory = System.getProperty("user.home");
      assertAll("parameter expansion",
        () -> assertEquals(homeDirectory, bourneShell.expandParameters("${HOME}"), "with braces"),
        () -> assertEquals(homeDirectory, bourneShell.expandParameters("$HOME"), "without braces"));
    }

    @Test
    @DisplayName("substitutes command with its result and keeps constant")
    void substituteCommandAndKeepConstant() {
      assertEquals("hello:1234", bourneShell.substituteCommands("$(echo hello):1234"));
    }

    @Test
    @DisplayName("substitutes command with its result and keeps constant")
    void keepConstantAndSubstituteCommand() {
      assertEquals("1234:world", bourneShell.substituteCommands("1234:$(echo world)"));
    }

  }

}
