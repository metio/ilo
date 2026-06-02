/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.os;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ParameterExpansion")
class ParameterExpansionTest {

  // NoOpExpansion inherits replace(...) unchanged, so it exercises the base-class logic
  // without requiring a shell binary on the host.
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

}
