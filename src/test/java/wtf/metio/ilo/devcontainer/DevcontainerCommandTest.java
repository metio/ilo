/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.devcontainer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import wtf.metio.devcontainer.Command;
import wtf.metio.devcontainer.DevcontainerBuilder;
import wtf.metio.ilo.shell.Docker;
import wtf.metio.ilo.shell.ShellOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Test
  void definitionReadsTheFileContents(@TempDir final Path directory) throws IOException {
    final var json = directory.resolve("devcontainer.json");
    Files.writeString(json, "{\"image\":\"example:test\"}");
    assertEquals("{\"image\":\"example:test\"}", DevcontainerCommand.definition(json));
  }

  @Test
  void definitionReturnsEmptyWhenUnreadable(@TempDir final Path directory) {
    assertEquals("", DevcontainerCommand.definition(directory.resolve("missing.json")));
  }

  @Nested
  @DisplayName("in-container lifecycle")
  class Lifecycle {

    private static final String CONTAINER = "ilo-shell-0123456789ab";

    private DevcontainerCommand command() {
      final var command = new DevcontainerCommand();
      command.options = new DevcontainerOptions();
      command.options.executeOnCreateCommand = true;
      command.options.executeUpdateContentCommand = true;
      command.options.executePostCreateCommand = true;
      command.options.executePostStartCommand = true;
      command.options.executePostAttachCommand = true;
      return command;
    }

    private List<List<String>> onCreate(final wtf.metio.devcontainer.Devcontainer devcontainer) {
      return command().lifecycle(new Docker(), CONTAINER, new ShellOptions(), devcontainer).onCreate();
    }

    @Test
    @DisplayName("runs a string command through the container's shell")
    void shouldExecStringThroughShell() {
      final var devcontainer = DevcontainerBuilder.builder()
          .onCreateCommand(Command.builder().string("npm install").create())
          .create();
      assertIterableEquals(
          List.of(List.of("docker", "exec", CONTAINER, "sh", "-c", "npm install")),
          onCreate(devcontainer));
    }

    @Test
    @DisplayName("runs an array command verbatim")
    void shouldExecArrayVerbatim() {
      final var devcontainer = DevcontainerBuilder.builder()
          .onCreateCommand(Command.builder().array(List.of("npm", "ci")).create())
          .create();
      assertIterableEquals(
          List.of(List.of("docker", "exec", CONTAINER, "npm", "ci")),
          onCreate(devcontainer));
    }

    @Test
    @DisplayName("runs each entry of an object command as its own exec")
    void shouldExecObjectEntries() {
      final var devcontainer = DevcontainerBuilder.builder()
          .onCreateCommand(Command.builder()
              .object(Map.of("only", Command.builder().string("setup").create()))
              .create())
          .create();
      assertIterableEquals(
          List.of(List.of("docker", "exec", CONTAINER, "sh", "-c", "setup")),
          onCreate(devcontainer));
    }

    @Test
    @DisplayName("collects onCreate, updateContent and postCreate into the creation phase")
    void shouldCollectCreationCommands() {
      final var devcontainer = DevcontainerBuilder.builder()
          .onCreateCommand(Command.builder().string("on-create").create())
          .updateContentCommand(Command.builder().string("update-content").create())
          .postCreateCommand(Command.builder().string("post-create").create())
          .create();
      assertIterableEquals(List.of(
          List.of("docker", "exec", CONTAINER, "sh", "-c", "on-create"),
          List.of("docker", "exec", CONTAINER, "sh", "-c", "update-content"),
          List.of("docker", "exec", CONTAINER, "sh", "-c", "post-create")), onCreate(devcontainer));
    }

    @Test
    @DisplayName("maps postStart to the start phase and postAttach to the attach phase")
    void shouldMapStartAndAttachPhases() {
      final var devcontainer = DevcontainerBuilder.builder()
          .postStartCommand(Command.builder().string("post-start").create())
          .postAttachCommand(Command.builder().string("post-attach").create())
          .create();
      final var lifecycle = command().lifecycle(new Docker(), CONTAINER, new ShellOptions(), devcontainer);
      assertIterableEquals(List.of(List.of("docker", "exec", CONTAINER, "sh", "-c", "post-start")), lifecycle.onStart());
      assertIterableEquals(List.of(List.of("docker", "exec", CONTAINER, "sh", "-c", "post-attach")), lifecycle.onAttach());
      assertTrue(lifecycle.onCreate().isEmpty());
    }

    @Test
    @DisplayName("omits a lifecycle command when its execute flag is disabled")
    void shouldRespectExecuteFlags() {
      final var devcontainer = DevcontainerBuilder.builder()
          .onCreateCommand(Command.builder().string("on-create").create())
          .create();
      final var command = command();
      command.options.executeOnCreateCommand = false;
      assertTrue(command.lifecycle(new Docker(), CONTAINER, new ShellOptions(), devcontainer).onCreate().isEmpty());
    }

    @Test
    @DisplayName("produces no lifecycle commands when the devcontainer declares none")
    void shouldBeEmptyWithoutCommands() {
      final var devcontainer = DevcontainerBuilder.builder().create();
      final var lifecycle = command().lifecycle(new Docker(), CONTAINER, new ShellOptions(), devcontainer);
      assertTrue(lifecycle.onCreate().isEmpty());
      assertTrue(lifecycle.onStart().isEmpty());
      assertTrue(lifecycle.onAttach().isEmpty());
    }

  }

}
