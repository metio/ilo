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

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static wtf.metio.ilo.os.ParameterExpansion.MATCHER_GROUP_NAME;

@DisplayName("PowerShell")
class PowerShellTest {

  @Test
  @DisplayName("builds a Write-Output command that reads the environment variable")
  void buildsParameterCommand() {
    assertEquals("Write-Output $env:HOME", PowerShell.parameterCommand("$HOME"));
  }

  @Test
  @DisplayName("expands the variable rather than echoing the command literally")
  void parameterCommandExpandsRatherThanEchoes() {
    final var command = PowerShell.parameterCommand("$USERNAME");
    assertAll(
        // A single-quoted command makes PowerShell print the literal text instead of expanding it.
        () -> assertFalse(command.startsWith("'"), command),
        // Environment variables are read through the 'env:' drive, not as plain '$USERNAME'.
        () -> assertTrue(command.contains("$env:USERNAME"), command));
  }

  @Nested
  @DisplayName("balanced command substitution")
  class BalancedTest {

    // The replacer is a marker, so the shell binary is never invoked: these tests run on any host.
    private final PowerShell shell = new PowerShell(Path.of("pwsh"));

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
      assertEquals("[Get-Date $(Get-Random)]",
          shell.substituteBalanced("$(Get-Date $(Get-Random))", inner -> "[" + inner + "]"));
    }

    @Test
    @DisplayName("leaves an unbalanced command substitution untouched")
    void leavesUnbalancedSubstitutionUntouched() {
      assertEquals("prefix:$(Get-Date",
          shell.substituteBalanced("prefix:$(Get-Date", inner -> "[" + inner + "]"));
    }

  }

  @Nested
  @DisplayName("regex")
  class Regex {

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
    @DisplayName("expands an environment variable to its value")
    void expandsEnvironmentVariable() {
      // OS is always set to "Windows_NT" on Windows; this exercises the real PowerShell, so it catches
      // a parameter command that echoes its text literally or reads '$OS' instead of '$env:OS'.
      assertEquals(System.getenv("OS"), powerShell.expandParameters("$OS"));
    }

    @Test
    @DisplayName("expands an environment variable surrounded by constants")
    void expandsEnvironmentVariableAmongConstants() {
      assertEquals(System.getenv("OS") + ":tail", powerShell.expandParameters("$OS:tail"));
    }

    @Test
    @DisplayName("expands multiple environment variables in one value")
    void expandsMultipleEnvironmentVariables() {
      assertEquals(System.getenv("OS") + ":" + System.getenv("USERNAME"),
          powerShell.expandParameters("$OS:$USERNAME"));
    }

    @Test
    @DisplayName("expands an unset environment variable to an empty string")
    void expandsUnsetVariableToEmpty() {
      assertEquals(":done", powerShell.expandParameters("$ILO_DEFINITELY_NOT_SET_VARIABLE:done"));
    }

    @Test
    @DisplayName("expands an environment variable through the expander backed by PowerShell")
    void expandsEnvironmentVariableThroughExpander() {
      // Force PowerShell rather than auto-detecting: a Windows CI runner also has Git Bash on PATH,
      // which auto-detection would pick instead.
      final var expander = OSSupport.expander(() -> OSSupport.powerShell().orElseThrow());
      assertEquals(System.getenv("OS"), expander.expand("$OS"));
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
