/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import wtf.metio.ilo.errors.RuntimeIOException;

import java.util.List;

class CliExecutorTest {

  @Test
  void shouldExecuteMissingCommands() {
    final var executor = new CliExecutor<>() {

      @Override
      public CliTool<?> selectRuntime(final Runtime runtime) {
        return null;
      }

    };

    Assertions.assertThrows(RuntimeIOException.class,
        () -> executor.execute(List.of("some", "command"), false));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  void shouldExecuteCommandOnUnix() {
    final var executor = new CliExecutor<>() {

      @Override
      public CliTool<?> selectRuntime(final Runtime runtime) {
        return null;
      }

    };

    Assertions.assertEquals(0, executor.execute(List.of("ls"), false));
  }

  @Test
  @EnabledOnOs({OS.WINDOWS})
  void shouldExecuteCommandOnWindows() {
    final var executor = new CliExecutor<>() {

      @Override
      public CliTool<?> selectRuntime(final Runtime runtime) {
        return null;
      }

    };

    Assertions.assertEquals(0, executor.execute(List.of("dir"), false));
  }

}
