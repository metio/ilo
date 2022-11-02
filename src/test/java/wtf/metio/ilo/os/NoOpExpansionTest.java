/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
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
