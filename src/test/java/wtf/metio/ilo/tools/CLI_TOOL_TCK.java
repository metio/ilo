/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.model.CliTool;
import wtf.metio.ilo.model.Options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class CLI_TOOL_TCK<OPTIONS extends Options, SHELL extends CliTool<OPTIONS>> {

  protected abstract SHELL tool();

  protected abstract OPTIONS options();

  protected abstract String name();

  @Test
  @DisplayName("has runtime name")
  void shouldHaveName() {
    assertEquals(name(), tool().name());
  }

  @Test
  @DisplayName("non-null pull arguments")
  void pullArguments() {
    assertNotNull(tool().pullArguments(options()));
  }

  @Test
  @DisplayName("non-null build arguments")
  void buildArguments() {
    assertNotNull(tool().buildArguments(options()));
  }

  @Test
  @DisplayName("non-null run arguments")
  void runArguments() {
    assertNotNull(tool().runArguments(options()));
  }

  @Test
  @DisplayName("non-null cleanup arguments")
  void cleanupArguments() {
    assertNotNull(tool().cleanupArguments(options()));
  }

}
