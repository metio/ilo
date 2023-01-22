/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import picocli.CommandLine;
import wtf.metio.devcontainer.Command;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DevcontainerCommandTest {

  @Test
  void shouldRunEmptyStringCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().string("").create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  void shouldRunStringCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().string("ls").create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  void shouldRunStringCommandWindows() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().string("dir").create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  void shouldRunComplexStringCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().string("echo foo='bar'").create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  void shouldRunFailingStringCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().string("sdlkfj").create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.USAGE, exitCode);
  }

  @Test
  void shouldRunEmptyArrayCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().array(List.of()).create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  void shouldRunArrayCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().array(List.of("ls")).create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  void shouldRunArrayCommandWindows() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().array(List.of("dir")).create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  void shouldRunComplexArrayCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().array(List.of("echo", "foo='bar'")).create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  void shouldRunFailingArrayCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().array(List.of("sdlkfj")).create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.USAGE, exitCode);
  }

  @Test
  void shouldRunEmptyObjectCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().object(Map.of()).create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  void shouldRunSingleStringObjectCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().object(Map.of("check", Command.builder().string("ls").create())).create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  void shouldRunSingleStringObjectCommandWindows() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().object(Map.of("check", Command.builder().string("dir").create())).create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  void shouldRunMultipleStringsObjectCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().object(
            Map.of("check", Command.builder().string("ls").create(),
                "echo", Command.builder().string("echo foo='bar'").create()))
        .create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  void shouldRunMultipleStringsObjectCommandWindows() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().object(
            Map.of("check", Command.builder().string("dir").create(),
                "echo", Command.builder().string("echo foo='bar'").create()))
        .create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  void shouldRunMixedObjectCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().object(
            Map.of("check", Command.builder().string("ls").create(),
                "echo", Command.builder().array(List.of("echo", "foo='bar'")).create()))
        .create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  void shouldRunMixedObjectCommandWindows() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().object(
            Map.of("check", Command.builder().string("dir").create(),
                "echo", Command.builder().array(List.of("echo", "foo='bar'")).create()))
        .create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.OK, exitCode);
  }

  @Test
  void shouldRunFailingStringObjectCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().object(
            Map.of("fail", Command.builder().string("sdlkfj").create()))
        .create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.USAGE, exitCode);
  }

  @Test
  void shouldRunFailingArrayObjectCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().object(
            Map.of("fail", Command.builder().array(List.of("sdlkfj")).create()))
        .create();
    final var exitCode = devcontainer.runCommand(command, false);
    assertEquals(CommandLine.ExitCode.USAGE, exitCode);
  }

}
