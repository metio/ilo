/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static wtf.metio.ilo.os.ParameterExpansion.MATCHER_GROUP_NAME;

@DisplayName("Bourne Shell")
class PosixShellTest {

  @Test
  @DisplayName("builds a quoted printf command so the parameter value is not word-split")
  void buildsQuotedParameterCommand() {
    assertEquals("printf '%s' \"$HOME\"", PosixShell.parameterCommand("$HOME"));
  }

  @Nested
  @DisplayName("regex")
  class RegexTest {

    @Test
    @DisplayName("regex for command using old style")
    void regexMatchesCommandWithOldStyle() {
      final var matcher = PosixShell.OLD_COMMAND_PATTERN.matcher("`some-command --with-option`");
      assertAll("old style",
          () -> assertTrue(matcher.find(), "matches"),
          () -> assertEquals("some-command --with-option", matcher.group(MATCHER_GROUP_NAME), "extraction"));
    }

    @Test
    @DisplayName("regex for commands using old style")
    void regexMatchesCommandsWithOldStyle() {
      final var matcher = PosixShell.OLD_COMMAND_PATTERN.matcher("`some-command --with-option`:`other --option`");
      assertAll("old style",
          () -> assertTrue(matcher.find(), "first match"),
          () -> assertEquals("some-command --with-option", matcher.group(MATCHER_GROUP_NAME), "first extraction"),
          () -> assertTrue(matcher.find(), "second match"),
          () -> assertEquals("other --option", matcher.group(MATCHER_GROUP_NAME), "second extraction"));
    }

    @Test
    @DisplayName("regex for parameter")
    void regexMatchesParameter() {
      final var matcher = PosixShell.PARAMETER_PATTERN.matcher("$HOME");
      assertAll("new style",
          () -> assertTrue(matcher.find(), "matches"),
          () -> assertEquals("$HOME", matcher.group(MATCHER_GROUP_NAME), "extraction"));
    }

    @Test
    @DisplayName("regex for parameters")
    void regexMatchesParameters() {
      final var matcher = PosixShell.PARAMETER_PATTERN.matcher("$HOME:$OTHER");
      assertAll("new style",
          () -> assertTrue(matcher.find(), "first matches"),
          () -> assertEquals("$HOME", matcher.group(MATCHER_GROUP_NAME), "first extraction"),
          () -> assertTrue(matcher.find(), "second matches"),
          () -> assertEquals("$OTHER", matcher.group(MATCHER_GROUP_NAME), "second extraction"));
    }

    @Test
    @DisplayName("regex for parameter with braces")
    void regexMatchesParameterWithBraces() {
      final var matcher = PosixShell.PARAMETER_WITH_BRACES_PATTERN.matcher("${HOME}");
      assertAll("new style",
          () -> assertTrue(matcher.find(), "matches"),
          () -> assertEquals("${HOME}", matcher.group(MATCHER_GROUP_NAME), "extraction"));
    }

    @Test
    @DisplayName("regex for parameters with braces")
    void regexMatchesParametersWithBraces() {
      final var matcher = PosixShell.PARAMETER_WITH_BRACES_PATTERN.matcher("${HOME}:${OTHER}");
      assertAll("new style",
          () -> assertTrue(matcher.find(), "matches"),
          () -> assertEquals("${HOME}", matcher.group(MATCHER_GROUP_NAME), "extraction"),
          () -> assertTrue(matcher.find(), "second matches"),
          () -> assertEquals("${OTHER}", matcher.group(MATCHER_GROUP_NAME), "second extraction"));
    }

  }

  @Nested
  @DisplayName("balanced command substitution")
  class BalancedTest {

    // The replacer is a marker, so the shell binary is never invoked: these tests run on any host.
    private final PosixShell shell = new PosixShell(Path.of("/bin/sh"));

    @Test
    @DisplayName("leaves a value without a command substitution untouched")
    void leavesPlainValueUntouched() {
      assertEquals("1000:1000", shell.substituteBalanced("1000:1000", inner -> "[" + inner + "]"));
    }

    @Test
    @DisplayName("substitutes a single command")
    void substitutesSingleCommand() {
      assertEquals("[some-command --with-option]",
          shell.substituteBalanced("$(some-command --with-option)", inner -> "[" + inner + "]"));
    }

    @Test
    @DisplayName("substitutes multiple commands")
    void substitutesMultipleCommands() {
      assertEquals("[some-command --with-option]:[other --option]",
          shell.substituteBalanced("$(some-command --with-option):$(other --option)", inner -> "[" + inner + "]"));
    }

    @Test
    @DisplayName("hands a nested command to the replacer as a single unit")
    void handsNestedCommandToReplacer() {
      // A regex bounded by '[^)]+' would stop at the first ')'; the balanced scan keeps the inner
      // '$(echo nested)' as part of the outer command's body for the shell to evaluate.
      assertEquals("[echo $(echo nested)]",
          shell.substituteBalanced("$(echo $(echo nested))", inner -> "[" + inner + "]"));
    }

    @Test
    @DisplayName("leaves an unbalanced command substitution untouched")
    void leavesUnbalancedSubstitutionUntouched() {
      assertEquals("prefix:$(echo unclosed",
          shell.substituteBalanced("prefix:$(echo unclosed", inner -> "[" + inner + "]"));
    }

  }

  @Nested
  @DisplayName("expansion")
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @ExtendWith(SystemStubsExtension.class)
  class ExpansionTest {

    private ParameterExpansion bourneShell;

    @BeforeEach
    void setUp() {
      bourneShell = OSSupport.posixShell().orElseThrow();
    }

    @Test
    @DisplayName("replaces parameter")
    void replacesParameter() {
      final var result = bourneShell.replace("$HOME:abc", input -> "test", PosixShell.PARAMETER_PATTERN);
      assertEquals("test:abc", result);
    }

    @Test
    @DisplayName("replaces parameter with braces")
    void replacesParameterWithBraces() {
      final var result = bourneShell.replace("${HOME}:abc", input -> "test", PosixShell.PARAMETER_WITH_BRACES_PATTERN);
      assertEquals("test:abc", result);
    }

    @Test
    @DisplayName("substitutes a nested command via the real shell")
    void substitutesNestedCommand() {
      assertEquals("nested", bourneShell.substituteCommands("$(echo $(echo nested))"));
    }

    @Test
    @DisplayName("keeps interior and leading whitespace in command output, trimming only trailing newlines")
    void keepsInteriorWhitespaceInCommandOutput() {
      assertEquals("  spaced  ", bourneShell.substituteCommands("$(printf '  spaced  \\n')"));
    }

    @Test
    @DisplayName("uses the output of a failing command as-is rather than aborting")
    void usesFailingCommandOutputAsIs() {
      assertEquals("partial", bourneShell.substituteCommands("$(printf partial; false)"));
    }

    @Test
    @DisplayName("replaces command with old style")
    void replacesCommandWithOldStyle() {
      final var result = bourneShell.replace("`id -u`:abc", input -> "test", PosixShell.OLD_COMMAND_PATTERN);
      assertEquals("test:abc", result);
    }

    @Test
    @DisplayName("expands ~ to the user's home directory")
    void expandHomeWithTilde(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      final var result = bourneShell.expandParameters("~/test:/something");
      assertEquals("/home/user/test:/something", result);
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

    @Test
    @DisplayName("substitutes command output that contains a dollar sign")
    void substituteCommandOutputWithDollar() {
      assertEquals("a$b", bourneShell.substituteCommands("$(printf 'a$b')"));
    }

    @Test
    @DisplayName("substitutes command output that contains a backslash")
    void substituteCommandOutputWithBackslash() {
      assertEquals("a\\b", bourneShell.substituteCommands("$(printf 'a\\\\b')"));
    }

    @Test
    @DisplayName("does not expand a tilde inside a path component")
    void doesNotExpandTildeInsidePath(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("/data/backup~2:/mnt", bourneShell.expandParameters("/data/backup~2:/mnt"));
    }

  }

}
