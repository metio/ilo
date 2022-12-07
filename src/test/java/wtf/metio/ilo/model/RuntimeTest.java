/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wtf.metio.ilo.compose.ComposeRuntime;
import wtf.metio.ilo.errors.NoMatchingRuntimeException;
import wtf.metio.ilo.shell.ShellRuntime;
import wtf.metio.ilo.test.TestMethodSources;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Runtime")
class RuntimeTest extends TestMethodSources {

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("finds first matching shell runtime")
  void findMatchingShellRuntime(final String runtime) {
    assertNotNull(Runtime.firstMatching(runtime, ShellRuntime.values()));
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  @DisplayName("finds first matching compose runtime")
  void findMatchingComposeRuntime(final String runtime) {
    assertNotNull(Runtime.firstMatching(runtime, ComposeRuntime.values()));
  }

  @Test
  @DisplayName("throws in case no match was found")
  void throwsForUnmatched() {
    assertThrows(NoMatchingRuntimeException.class,
        () -> Runtime.firstMatching("not-found", ComposeRuntime.values()));
  }

}
