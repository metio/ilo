/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import wtf.metio.ilo.cli.Executables;
import wtf.metio.ilo.errors.RuntimeIOException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
  @DisplayName("Should detect tool in PATH")
  void shouldExecuteCommandOnLinux() {
    final var executor = new CliExecutor<>() {

      @Override
      public CliTool<?> selectRuntime(final Runtime runtime) {
        return null;
      }

    };

    Assertions.assertEquals(0, executor.execute(List.of("ls"), false));
  }

}
