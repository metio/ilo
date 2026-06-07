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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import wtf.metio.devcontainer.BuildBuilder;
import wtf.metio.devcontainer.Command;
import wtf.metio.devcontainer.DevcontainerBuilder;
import wtf.metio.devcontainer.Mount;
import wtf.metio.devcontainer.UserEnvProbe;
import wtf.metio.ilo.shell.Docker;
import wtf.metio.ilo.shell.ShellOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("runs a string command through the host shell, honoring shell operators")
  void runsStringCommandInShell() {
    final var devcontainer = new DevcontainerCommand();
    // 'false || true' exits 0 only when a shell evaluates it; tokenized, 'false' ignores the rest.
    final var command = Command.builder().string("false || true").create();
    assertEquals(CommandLine.ExitCode.OK, devcontainer.runCommand(command, false));
  }

  @Test
  @ExtendWith(SystemStubsExtension.class)
  @DisplayName("reports a clear message and USAGE when the devcontainer.json declares nothing to open")
  void warnsWhenNothingToOpen(@TempDir final Path directory, final SystemProperties properties,
      final SystemErr systemErr) throws Exception {
    Files.writeString(directory.resolve("devcontainer.json"), "{}");
    properties.set("user.dir", directory.toString());
    final var command = new DevcontainerCommand();
    command.options = new DevcontainerOptions();
    command.options.locations = List.of("devcontainer.json");

    final var exitCode = command.call();

    assertEquals(CommandLine.ExitCode.USAGE, exitCode);
    assertTrue(systemErr.getText().contains("nothing for ilo to open"), systemErr.getText());
  }

  @Test
  @ExtendWith(SystemStubsExtension.class)
  @DisplayName("reports a clear message and USAGE when a compose-based devcontainer.json declares no service")
  void warnsWhenComposeHasNoService(@TempDir final Path directory, final SystemProperties properties,
      final SystemErr systemErr) throws Exception {
    Files.writeString(directory.resolve("devcontainer.json"), "{\"dockerComposeFile\":[\"docker-compose.yml\"]}");
    properties.set("user.dir", directory.toString());
    final var command = new DevcontainerCommand();
    command.options = new DevcontainerOptions();
    command.options.locations = List.of("devcontainer.json");

    final var exitCode = command.call();

    assertEquals(CommandLine.ExitCode.USAGE, exitCode);
    assertTrue(systemErr.getText().contains("service"), systemErr.getText());
  }

  @Test
  void shouldRunFailingStringCommand() {
    final var devcontainer = new DevcontainerCommand();
    final var command = Command.builder().string("sdlkfj").create();
    final var exitCode = devcontainer.runCommand(command, false);
    // The shell runs the command and reports its failure (e.g. 127 for "command not found").
    assertNotEquals(CommandLine.ExitCode.OK, exitCode);
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
    // The shell runs the command and reports its failure (e.g. 127 for "command not found").
    assertNotEquals(CommandLine.ExitCode.OK, exitCode);
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
  void unsupportedFieldsListsOnlyTrulyUnsupportedFields() {
    // remoteEnv and userEnvProbe are now supported, so they are not reported even when present.
    final var devcontainer = DevcontainerBuilder.builder()
        .features(Map.of("ghcr.io/devcontainers/features/node:1", Map.of("version", "lts")))
        .remoteEnv(Map.of("FOO", "bar"))
        .userEnvProbe(UserEnvProbe.loginInteractiveShell)
        .create();
    assertIterableEquals(List.of("features"),
        DevcontainerCommand.unsupportedFields(devcontainer));
  }

  @Test
  void unsupportedFieldsReportsFieldsDroppedOnTheComposePath() {
    // The compose path delegates container creation to docker-compose/podman-compose, so the fields
    // ilo applies itself on the image/Dockerfile path have no effect and must be reported.
    final var devcontainer = DevcontainerBuilder.builder()
        .dockerComposeFile(List.of("docker-compose.yml"))
        .service("app")
        .onCreateCommand(Command.builder().string("on-create").create())
        .updateContentCommand(Command.builder().string("update-content").create())
        .postCreateCommand(Command.builder().string("npm install").create())
        .postStartCommand(Command.builder().string("post-start").create())
        .postAttachCommand(Command.builder().string("post-attach").create())
        .containerEnv(Map.of("FOO", "bar"))
        .remoteEnv(Map.of("BAR", "baz"))
        .userEnvProbe(UserEnvProbe.loginInteractiveShell)
        .forwardPorts(List.of("8080"))
        .appPort(List.of("9090"))
        .runArgs(List.of("--privileged"))
        .mounts(List.of(new Mount("type=bind,source=/a,target=/b", null)))
        .create();

    assertIterableEquals(List.of(
            "onCreateCommand", "updateContentCommand", "postCreateCommand", "postStartCommand",
            "postAttachCommand", "containerEnv", "remoteEnv", "userEnvProbe", "forwardPorts",
            "appPort", "runArgs", "mounts"),
        DevcontainerCommand.unsupportedFields(devcontainer));
  }

  @Test
  void unsupportedFieldsDoesNotReportLifecycleFieldsOnTheShellPath() {
    // On the image/Dockerfile path these fields are honored, so they must not be reported.
    final var devcontainer = DevcontainerBuilder.builder()
        .image("example:1")
        .postCreateCommand(Command.builder().string("npm install").create())
        .containerEnv(Map.of("FOO", "bar"))
        .remoteEnv(Map.of("BAR", "baz"))
        .userEnvProbe(UserEnvProbe.loginInteractiveShell)
        .forwardPorts(List.of("8080"))
        .runArgs(List.of("--privileged"))
        .create();

    assertTrue(DevcontainerCommand.unsupportedFields(devcontainer).isEmpty());
  }

  @Test
  void unsupportedFieldsReportsFeaturesAlongsideComposeDroppedFields() {
    final var devcontainer = DevcontainerBuilder.builder()
        .dockerComposeFile(List.of("docker-compose.yml"))
        .features(Map.of("ghcr.io/devcontainers/features/node:1", Map.of("version", "lts")))
        .postCreateCommand(Command.builder().string("npm install").create())
        .create();

    assertIterableEquals(List.of("features", "postCreateCommand"),
        DevcontainerCommand.unsupportedFields(devcontainer));
  }

  @Test
  void unsupportedFieldsIgnoresEmptyComposeFileList() {
    final var devcontainer = DevcontainerBuilder.builder()
        .dockerComposeFile(List.of())
        .postCreateCommand(Command.builder().string("npm install").create())
        .create();

    assertTrue(DevcontainerCommand.unsupportedFields(devcontainer).isEmpty());
  }

  @Test
  void unsupportedFieldsIsEmptyWhenNonePresent() {
    assertTrue(DevcontainerCommand.unsupportedFields(DevcontainerBuilder.builder().create()).isEmpty());
  }

  @Test
  void unsupportedFieldsIgnoresEmptyCollections() {
    final var devcontainer = DevcontainerBuilder.builder()
        .features(Map.of())
        .remoteEnv(Map.of())
        .create();
    assertTrue(DevcontainerCommand.unsupportedFields(devcontainer).isEmpty());
  }

  @Test
  void usesShellPathForAnImageDevcontainer() {
    final var devcontainer = DevcontainerBuilder.builder().image("example:1").create();
    assertTrue(DevcontainerCommand.usesImageOrDockerfile(devcontainer));
  }

  @Test
  void usesShellPathForADockerfileOnlyDevcontainer() {
    final var devcontainer = DevcontainerBuilder.builder()
        .build(BuildBuilder.builder().dockerfile("Dockerfile").create())
        .create();
    assertTrue(DevcontainerCommand.usesImageOrDockerfile(devcontainer));
  }

  @Test
  void doesNotUseShellPathWithoutImageOrDockerfile() {
    assertFalse(DevcontainerCommand.usesImageOrDockerfile(DevcontainerBuilder.builder().create()));
  }

  @Test
  void imageTagKeepsAnExplicitImage() {
    final var devcontainer = DevcontainerBuilder.builder().image("example:1").create();
    assertEquals("example:1", DevcontainerCommand.imageTag(devcontainer, Paths.get("/p/.devcontainer/devcontainer.json")));
  }

  @Test
  void imageTagSynthesizesAStableTagForADockerfileOnlyDevcontainer() {
    final var devcontainer = DevcontainerBuilder.builder()
        .build(BuildBuilder.builder().dockerfile("Dockerfile").create())
        .create();
    final var json = Paths.get("/p/.devcontainer/devcontainer.json");
    final var tag = DevcontainerCommand.imageTag(devcontainer, json);
    assertTrue(tag.startsWith("ilo-devcontainer-"), tag);
    assertEquals(tag, DevcontainerCommand.imageTag(devcontainer, json), "tag must be stable for the same definition");
  }

  @Test
  void rootMessagePrefersTheCauseMessage() {
    final var exception = new CompletionException("wrapper", new RuntimeException("boom"));
    assertEquals("boom", DevcontainerCommand.rootMessage(exception));
  }

  @Test
  void rootMessageFallsBackToTheExceptionMessageWhenTheCauseHasNone() {
    final var exception = new CompletionException("outer", new RuntimeException());
    assertEquals("outer", DevcontainerCommand.rootMessage(exception));
  }

  @Test
  void rootMessageUsesTheExceptionMessageWhenThereIsNoCause() {
    final var exception = new CompletionException("only", null);
    assertEquals("only", DevcontainerCommand.rootMessage(exception));
  }

  @Test
  void rootMessageFallsBackToTheTypeWhenNoMessageIsAvailable() {
    final var exception = new CompletionException(null, null);
    assertTrue(DevcontainerCommand.rootMessage(exception).contains("CompletionException"));
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

    private List<List<List<String>>> onCreate(final wtf.metio.devcontainer.Devcontainer devcontainer) {
      return command().lifecycle(new Docker(), CONTAINER, new ShellOptions(), devcontainer).onCreate();
    }

    @Test
    @DisplayName("runs a string command through the container's shell")
    void shouldExecStringThroughShell() {
      final var devcontainer = DevcontainerBuilder.builder()
          .onCreateCommand(Command.builder().string("npm install").create())
          .create();
      assertIterableEquals(
          List.of(List.of(List.of("docker", "exec", CONTAINER, "sh", "-c", "npm install"))),
          onCreate(devcontainer));
    }

    @Test
    @DisplayName("runs an array command verbatim")
    void shouldExecArrayVerbatim() {
      final var devcontainer = DevcontainerBuilder.builder()
          .onCreateCommand(Command.builder().array(List.of("npm", "ci")).create())
          .create();
      assertIterableEquals(
          List.of(List.of(List.of("docker", "exec", CONTAINER, "npm", "ci"))),
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
          List.of(List.of(List.of("docker", "exec", CONTAINER, "sh", "-c", "setup"))),
          onCreate(devcontainer));
    }

    @Test
    @DisplayName("groups the entries of an object command into one parallel step")
    void shouldGroupObjectEntriesIntoOneStep() {
      final var devcontainer = DevcontainerBuilder.builder()
          .onCreateCommand(Command.builder()
              .object(Map.of(
                  "a", Command.builder().string("setup-a").create(),
                  "b", Command.builder().string("setup-b").create()))
              .create())
          .create();
      final var steps = onCreate(devcontainer);
      assertEquals(1, steps.size(), "an object command is a single parallel step");
      final var group = steps.get(0);
      assertEquals(2, group.size(), "both entries belong to the same step");
      assertTrue(group.contains(List.of("docker", "exec", CONTAINER, "sh", "-c", "setup-a")), group.toString());
      assertTrue(group.contains(List.of("docker", "exec", CONTAINER, "sh", "-c", "setup-b")), group.toString());
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
          List.of(List.of("docker", "exec", CONTAINER, "sh", "-c", "on-create")),
          List.of(List.of("docker", "exec", CONTAINER, "sh", "-c", "update-content")),
          List.of(List.of("docker", "exec", CONTAINER, "sh", "-c", "post-create"))), onCreate(devcontainer));
    }

    @Test
    @DisplayName("maps postStart to the start phase and postAttach to the attach phase")
    void shouldMapStartAndAttachPhases() {
      final var devcontainer = DevcontainerBuilder.builder()
          .postStartCommand(Command.builder().string("post-start").create())
          .postAttachCommand(Command.builder().string("post-attach").create())
          .create();
      final var lifecycle = command().lifecycle(new Docker(), CONTAINER, new ShellOptions(), devcontainer);
      assertIterableEquals(List.of(List.of(List.of("docker", "exec", CONTAINER, "sh", "-c", "post-start"))), lifecycle.onStart());
      assertIterableEquals(List.of(List.of(List.of("docker", "exec", CONTAINER, "sh", "-c", "post-attach"))), lifecycle.onAttach());
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
