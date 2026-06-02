/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ParameterExpansion")
class ParameterExpansionTest {

  // NoOpExpansion inherits replace(...) and expandTilde(...) unchanged, so they exercise the
  // base-class logic without requiring a shell binary on the host.
  private final ParameterExpansion expansion = new NoOpExpansion();

  @Test
  @DisplayName("replaces a single match with the replacer result")
  void replacesSingleMatch() {
    final var result = expansion.replace("$HOME:abc", input -> "value", PosixShell.PARAMETER_PATTERN);
    assertEquals("value:abc", result);
  }

  @Test
  @DisplayName("keeps the input unchanged when nothing matches")
  void keepsInputWithoutMatches() {
    final var result = expansion.replace("1000:1000", input -> "value", PosixShell.PARAMETER_PATTERN);
    assertEquals("1000:1000", result);
  }

  @Test
  @DisplayName("applies every pattern in the given order")
  void appliesAllPatterns() {
    final var result = expansion.replace("$(cmd):$HOME", input -> "X",
        PosixShell.NEW_COMMAND_PATTERN, PosixShell.PARAMETER_PATTERN);
    assertEquals("X:X", result);
  }

  @Test
  @DisplayName("treats a dollar sign in the replacement as a literal")
  void keepsDollarInReplacementLiteral() {
    final var result = expansion.replace("$HOME:abc", input -> "$5 dollars", PosixShell.PARAMETER_PATTERN);
    assertEquals("$5 dollars:abc", result);
  }

  @Test
  @DisplayName("treats a backslash in the replacement as a literal")
  void keepsBackslashInReplacementLiteral() {
    final var result = expansion.replace("$HOME:abc", input -> "C:\\Users\\me", PosixShell.PARAMETER_PATTERN);
    assertEquals("C:\\Users\\me:abc", result);
  }

  @Test
  @DisplayName("treats a named group reference in the replacement as a literal")
  void keepsGroupReferenceInReplacementLiteral() {
    final var result = expansion.replace("$HOME:abc", input -> "${missing}", PosixShell.PARAMETER_PATTERN);
    assertEquals("${missing}:abc", result);
  }

  @Nested
  @DisplayName("tilde expansion")
  @ExtendWith(SystemStubsExtension.class)
  class TildeExpansion {

    @Test
    @DisplayName("expands a leading tilde followed by a slash")
    void expandsLeadingTildeWithSlash(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("/home/user/work", expansion.expandTilde("~/work"));
    }

    @Test
    @DisplayName("expands a bare tilde")
    void expandsBareTilde(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("/home/user", expansion.expandTilde("~"));
    }

    @Test
    @DisplayName("expands a tilde after a colon separator")
    void expandsTildeAfterColon(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("/data:/home/user/cache", expansion.expandTilde("/data:~/cache"));
    }

    @Test
    @DisplayName("expands a tilde after an equals separator")
    void expandsTildeAfterEquals(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("DIR=/home/user/x", expansion.expandTilde("DIR=~/x"));
    }

    @Test
    @DisplayName("expands a tilde at the end of a segment")
    void expandsTildeAtSegmentEnd(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("/home/user:/data", expansion.expandTilde("~:/data"));
    }

    @Test
    @DisplayName("does not expand a tilde inside a path component")
    void keepsTildeInsidePath(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("/data/backup~2:/mnt", expansion.expandTilde("/data/backup~2:/mnt"));
    }

    @Test
    @DisplayName("does not expand a tilde that introduces another user's name")
    void keepsOtherUserTilde(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("~bob/work", expansion.expandTilde("~bob/work"));
    }

    @Test
    @DisplayName("keeps a backslash in the home directory literal")
    void keepsBackslashInHome(final SystemProperties properties) {
      properties.set("user.home", "C:\\Users\\me");
      assertEquals("C:\\Users\\me/work", expansion.expandTilde("~/work"));
    }

  }

}
