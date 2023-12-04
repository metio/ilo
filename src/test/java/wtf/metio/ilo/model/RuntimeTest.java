/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wtf.metio.ilo.shell.ShellRuntime;
import wtf.metio.ilo.test.TestMethodSources;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Runtime")
class RuntimeTest extends TestMethodSources {

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  @DisplayName("finds first matching shell runtime")
  void findMatchingShellRuntime(final String runtime) {
    assertNotNull(Runtime.firstMatching(runtime, ShellRuntime.values()));
  }

}
