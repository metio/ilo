/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.model.CliTool;
import wtf.metio.ilo.model.Options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class CliToolTCK<OPTIONS extends Options, SHELL extends CliTool<OPTIONS>> {

  protected abstract SHELL tool();

  protected abstract OPTIONS options();

  protected abstract String name();

  protected String command() {
    return "";
  }

  @Test
  @DisplayName("has runtime name")
  void shouldHaveName() {
    assertEquals(name(), tool().name());
  }

  @Test
  @DisplayName("has subcommand")
  void shouldHaveCommand() {
    assertEquals(command(), tool().command());
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
