/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("NoOpExpansion")
class NoOpExpansionTest {

  private ParameterExpansion expansion;

  @BeforeEach
  void setUp() {
    expansion = new NoOpExpansion();
  }

  @Test
  @DisplayName("returns a command substitution verbatim")
  void keepsCommand() {
    assertEquals("$(git diff)", expansion.expand("$(git diff)"));
  }

  @Test
  @DisplayName("returns a parameter reference verbatim")
  void keepsParameter() {
    assertEquals("${HOME}", expansion.expand("${HOME}"));
  }

  @Test
  @DisplayName("returns a leading tilde verbatim")
  void keepsTilde() {
    assertEquals("~/work", expansion.expand("~/work"));
  }

}
