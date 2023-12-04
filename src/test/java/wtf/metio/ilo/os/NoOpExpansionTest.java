/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.os;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NoOpExpansionTest {

  private ParameterExpansion expansion;

  @BeforeEach
  void setUp() {
    expansion = new NoOpExpansion();
  }

  @Test
  void shouldNotSubstituteCommand() {
    final var command = "$(git diff)";
    final var result = expansion.substituteCommands(command);
    Assertions.assertEquals(command, result);
  }

  @Test
  void shouldNotExpandParameter() {
    final var command = "${HOME}";
    final var result = expansion.expandParameters(command);
    Assertions.assertEquals(command, result);
  }

}
