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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Test
  @DisplayName("expands a leading tilde before a Windows backslash path")
  @ExtendWith(SystemStubsExtension.class)
  void expandsWindowsBackslashTilde(final SystemProperties properties) {
    properties.set("user.home", "C:\\Users\\me");
    final var shell = new PowerShell(Path.of("pwsh"));
    assertEquals("C:\\Users\\me\\work", shell.expandTilde("~\\work"));
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
    @DisplayName("expands an environment variable to its value")
    void expandsEnvironmentVariable() {
      // OS is always set to "Windows_NT" on Windows; this exercises the real PowerShell, so it catches
      // a parameter command that echoes its text literally or reads '$OS' instead of '$env:OS'.
      assertEquals(System.getenv("OS"), powerShell.expand("$OS"));
    }

    @Test
    @DisplayName("expands a braced environment variable to its value")
    void expandsBracedEnvironmentVariable() {
      assertEquals(System.getenv("OS"), powerShell.expand("${OS}"));
    }

    @Test
    @DisplayName("expands an environment variable surrounded by constants")
    void expandsEnvironmentVariableAmongConstants() {
      assertEquals(System.getenv("OS") + ":tail", powerShell.expand("$OS:tail"));
    }

    @Test
    @DisplayName("expands multiple environment variables in one value")
    void expandsMultipleEnvironmentVariables() {
      assertEquals(System.getenv("OS") + ":" + System.getenv("USERNAME"),
          powerShell.expand("$OS:$USERNAME"));
    }

    @Test
    @DisplayName("expands an unset environment variable to an empty string")
    void expandsUnsetVariableToEmpty() {
      assertEquals(":done", powerShell.expand("$ILO_DEFINITELY_NOT_SET_VARIABLE:done"));
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
    @DisplayName("keeps constants as-is")
    void keepsConstants() {
      assertEquals("1000:1000", powerShell.expand("1000:1000"));
    }

    @Test
    @DisplayName("substitutes commands with their results")
    void substitutesCommands() {
      assertEquals("hello:world", powerShell.expand("$(echo hello):$(echo world)"));
    }

    @Test
    @DisplayName("substitutes a command and keeps a surrounding constant")
    void substitutesCommandAmongConstants() {
      assertAll(
          () -> assertEquals("hello:1234", powerShell.expand("$(echo hello):1234")),
          () -> assertEquals("1234:world", powerShell.expand("1234:$(echo world)")));
    }
  }

}
