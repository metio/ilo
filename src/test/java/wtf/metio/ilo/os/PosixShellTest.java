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

@DisplayName("Bourne Shell")
class PosixShellTest {

  @Test
  @DisplayName("builds a quoted printf command so the parameter value is not word-split")
  void buildsQuotedParameterCommand() {
    assertEquals("printf '%s' \"$HOME\"", PosixShell.parameterCommand("$HOME"));
  }

  @Test
  @DisplayName("does not treat a backslash after a tilde as a home-directory separator")
  @ExtendWith(SystemStubsExtension.class)
  void keepsBackslashTilde(final SystemProperties properties) {
    properties.set("user.home", "/home/user");
    final var shell = new PosixShell(Path.of("sh"));
    assertEquals("~\\work", shell.expandTilde("~\\work"));
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
    @DisplayName("expands a parameter, with and without braces")
    void expandsParameter() {
      final var home = System.getProperty("user.home");
      assertAll("parameter expansion",
          () -> assertEquals(home, bourneShell.expand("${HOME}"), "with braces"),
          () -> assertEquals(home, bourneShell.expand("$HOME"), "without braces"));
    }

    @Test
    @DisplayName("expands ~ to the user's home directory")
    void expandsTilde(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("/home/user/test:/something", bourneShell.expand("~/test:/something"));
    }

    @Test
    @DisplayName("keeps a tilde inside a path component")
    void keepsTildeInsidePath(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("/data/backup~2:/mnt", bourneShell.expand("/data/backup~2:/mnt"));
    }

    @Test
    @DisplayName("keeps constants as-is")
    void keepsConstants() {
      assertEquals("1000:1000", bourneShell.expand("1000:1000"));
    }

    @Test
    @DisplayName("substitutes commands with their results")
    void substitutesCommands() {
      assertEquals("hello:world", bourneShell.expand("$(echo hello):$(echo world)"));
    }

    @Test
    @DisplayName("substitutes a command and keeps a surrounding constant")
    void substitutesCommandAmongConstants() {
      assertAll(
          () -> assertEquals("hello:1234", bourneShell.expand("$(echo hello):1234")),
          () -> assertEquals("1234:world", bourneShell.expand("1234:$(echo world)")));
    }

    @Test
    @DisplayName("substitutes an old-style backtick command")
    void substitutesBacktickCommand() {
      assertEquals("hello:abc", bourneShell.expand("`echo hello`:abc"));
    }

    @Test
    @DisplayName("substitutes a nested command via the shell")
    void substitutesNestedCommand() {
      assertEquals("nested", bourneShell.expand("$(echo $(echo nested))"));
    }

    @Test
    @DisplayName("expands a parameter inside a command body through the shell")
    void expandsParameterInsideCommand() {
      final var home = System.getProperty("user.home");
      assertEquals(home, bourneShell.expand("$(echo $HOME)"));
    }

    @Test
    @DisplayName("keeps interior and leading whitespace in command output, trimming only trailing newlines")
    void keepsInteriorWhitespace() {
      assertEquals("  spaced  ", bourneShell.expand("$(printf '  spaced  \\n')"));
    }

    @Test
    @DisplayName("uses the output of a failing command as-is rather than aborting")
    void usesFailingCommandOutput() {
      assertEquals("partial", bourneShell.expand("$(printf partial; false)"));
    }

    @Test
    @DisplayName("appends command output containing a dollar sign verbatim")
    void outputWithDollar() {
      assertEquals("a$b", bourneShell.expand("$(printf 'a$b')"));
    }

    @Test
    @DisplayName("appends command output containing a backslash verbatim")
    void outputWithBackslash() {
      assertEquals("a\\b", bourneShell.expand("$(printf 'a\\\\b')"));
    }

    @Test
    @DisplayName("does not re-execute backticks contained in a command's output")
    void doesNotReExecuteBackticksInOutput() {
      // The inner 'echo X' sits inside single quotes, so the command prints the backticks literally.
      // A second backtick pass over that output would run 'echo X'; the single scan must not.
      assertEquals("a`echo X`b", bourneShell.expand("$(printf 'a`echo X`b')"));
    }
  }

}
