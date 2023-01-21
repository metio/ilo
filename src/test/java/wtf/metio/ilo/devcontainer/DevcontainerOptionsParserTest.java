/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;
import wtf.metio.ilo.compose.ComposeRuntime;
import wtf.metio.ilo.shell.ShellRuntime;
import wtf.metio.ilo.test.TestMethodSources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DevcontainerOptionsParserTest extends TestMethodSources {

  @CommandLine.Command
  static class TestCommand implements Runnable {

    @CommandLine.Mixin
    public DevcontainerOptions options;

    @Override
    public void run() {
    }

  }

  @Test
  public void defaultValues() {
    final var command = parseCommandOptions();
    assertAll(
        () -> assertNull(command.options.shellRuntime, "shellRuntime"),
        () -> assertNull(command.options.composeRuntime, "composeRuntime"),
        () -> assertFalse(command.options.debug, "debug"),
        () -> assertFalse(command.options.pull, "pull"),
        () -> assertTrue(command.options.mountProjectDir, "mountProjectDir"),
        () -> assertFalse(command.options.removeImage, "removeImage"),
        () -> assertTrue(command.options.executeInitializeCommand, "executeInitializeCommand"),
        () -> assertTrue(command.options.executeOnCreateCommand, "executeOnCreateCommand"),
        () -> assertTrue(command.options.executeUpdateContentCommand, "executeUpdateContentCommand"),
        () -> assertTrue(command.options.executePostCreateCommand, "executePostCreateCommand"),
        () -> assertTrue(command.options.executePostStartCommand, "executePostStartCommand"),
        () -> assertTrue(command.options.executePostAttachCommand, "executePostAttachCommand"),
        () -> assertIterableEquals(List.of(".devcontainer/devcontainer.json", ".devcontainer.json"), command.options.locations, "locations")
    );
  }

  @ParameterizedTest
  @MethodSource("shellRuntimes")
  void shellRuntime(final String runtime) {
    final var command = parseCommandOptions("--shell-runtime", runtime);
    assertEquals(ShellRuntime.fromAlias(runtime), command.options.shellRuntime, "shellRuntime");
  }

  @ParameterizedTest
  @MethodSource("composeRuntimes")
  void composeRuntime(final String runtime) {
    final var command = parseCommandOptions("--compose-runtime", runtime);
    assertEquals(ComposeRuntime.fromAlias(runtime), command.options.composeRuntime, "composeRuntime");
  }

  @Test
  public void debug() {
    final var command = parseCommandOptions("--debug");
    assertTrue(command.options.debug, "debug");
  }

  @Test
  public void debugTrue() {
    final var command = parseCommandOptions("--debug=true");
    assertTrue(command.options.debug, "debug");
  }

  @Test
  public void debugFalse() {
    final var command = parseCommandOptions("--debug=false");
    assertFalse(command.options.debug, "debug");
  }

  @Test
  public void pull() {
    final var command = parseCommandOptions("--pull");
    assertTrue(command.options.pull, "pull");
  }

  @Test
  public void pullTrue() {
    final var command = parseCommandOptions("--pull=true");
    assertTrue(command.options.pull, "pull");
  }

  @Test
  public void pullFalse() {
    final var command = parseCommandOptions("--pull=false");
    assertFalse(command.options.pull, "pull");
  }

  @Test
  public void removeImage() {
    final var command = parseCommandOptions("--remove-image");
    assertTrue(command.options.removeImage, "removeImage");
  }

  @Test
  public void removeImageTrue() {
    final var command = parseCommandOptions("--remove-image=true");
    assertTrue(command.options.removeImage, "removeImage");
  }

  @Test
  public void removeImageFalse() {
    final var command = parseCommandOptions("--remove-image=false");
    assertFalse(command.options.removeImage, "removeImage");
  }

  @Test
  public void mountProjectDir() {
    final var command = parseCommandOptions("--mount-project-dir");
    assertTrue(command.options.mountProjectDir, "mountProjectDir");
  }

  @Test
  public void mountProjectDirTrue() {
    final var command = parseCommandOptions("--mount-project-dir=true");
    assertTrue(command.options.mountProjectDir, "mountProjectDir");
  }

  @Test
  public void mountProjectDirFalse() {
    final var command = parseCommandOptions("--mount-project-dir=false");
    assertFalse(command.options.mountProjectDir, "mountProjectDir");
  }

  @Test
  public void mountProjectDirNegated() {
    final var command = parseCommandOptions("--no-mount-project-dir");
    assertFalse(command.options.mountProjectDir, "mountProjectDir");
  }

  @Test
  public void mountProjectDirNegatedTrue() {
    final var command = parseCommandOptions("--no-mount-project-dir=true");
    assertFalse(command.options.mountProjectDir, "mountProjectDir");
  }

  @Test
  public void mountProjectDirNegatedFalse() {
    final var command = parseCommandOptions("--no-mount-project-dir=false");
    assertTrue(command.options.mountProjectDir, "mountProjectDir");
  }

  @Test
  public void executeInitializeCommand() {
    final var command = parseCommandOptions("--execute-initialize-command");
    assertTrue(command.options.executeInitializeCommand, "executeInitializeCommand");
  }

  @Test
  public void executeInitializeCommandTrue() {
    final var command = parseCommandOptions("--execute-initialize-command=true");
    assertTrue(command.options.executeInitializeCommand, "executeInitializeCommand");
  }

  @Test
  public void executeInitializeCommandFalse() {
    final var command = parseCommandOptions("--execute-initialize-command=false");
    assertFalse(command.options.executeInitializeCommand, "executeInitializeCommand");
  }

  @Test
  public void executeInitializeCommandNegated() {
    final var command = parseCommandOptions("--no-execute-initialize-command");
    assertFalse(command.options.executeInitializeCommand, "executeInitializeCommand");
  }

  @Test
  public void executeInitializeCommandNegatedTrue() {
    final var command = parseCommandOptions("--no-execute-initialize-command=true");
    assertFalse(command.options.executeInitializeCommand, "executeInitializeCommand");
  }

  @Test
  public void executeInitializeCommandNegatedFalse() {
    final var command = parseCommandOptions("--no-execute-initialize-command=false");
    assertTrue(command.options.executeInitializeCommand, "executeInitializeCommand");
  }

  @Test
  public void executeOnCreateCommand() {
    final var command = parseCommandOptions("--execute-on-create-command");
    assertTrue(command.options.executeOnCreateCommand, "executeOnCreateCommand");
  }

  @Test
  public void executeOnCreateCommandTrue() {
    final var command = parseCommandOptions("--execute-on-create-command=true");
    assertTrue(command.options.executeOnCreateCommand, "executeOnCreateCommand");
  }

  @Test
  public void executeOnCreateCommandFalse() {
    final var command = parseCommandOptions("--execute-on-create-command=false");
    assertFalse(command.options.executeOnCreateCommand, "executeOnCreateCommand");
  }

  @Test
  public void executeOnCreateCommandNegated() {
    final var command = parseCommandOptions("--no-execute-on-create-command");
    assertFalse(command.options.executeOnCreateCommand, "executeOnCreateCommand");
  }

  @Test
  public void executeOnCreateCommandNegatedTrue() {
    final var command = parseCommandOptions("--no-execute-on-create-command=true");
    assertFalse(command.options.executeOnCreateCommand, "executeOnCreateCommand");
  }

  @Test
  public void executeOnCreateCommandNegatedFalse() {
    final var command = parseCommandOptions("--no-execute-on-create-command=false");
    assertTrue(command.options.executeOnCreateCommand, "executeOnCreateCommand");
  }

  @Test
  public void executeUpdateContentCommand() {
    final var command = parseCommandOptions("--execute-update-content-command");
    assertTrue(command.options.executeUpdateContentCommand, "executeUpdateContentCommand");
  }

  @Test
  public void executeUpdateContentCommandTrue() {
    final var command = parseCommandOptions("--execute-update-content-command=true");
    assertTrue(command.options.executeUpdateContentCommand, "executeUpdateContentCommand");
  }

  @Test
  public void executeUpdateContentCommandFalse() {
    final var command = parseCommandOptions("--execute-update-content-command=false");
    assertFalse(command.options.executeUpdateContentCommand, "executeUpdateContentCommand");
  }

  @Test
  public void executeUpdateContentCommandNegated() {
    final var command = parseCommandOptions("--no-execute-update-content-command");
    assertFalse(command.options.executeUpdateContentCommand, "executeUpdateContentCommand");
  }

  @Test
  public void executeUpdateContentCommandNegatedTrue() {
    final var command = parseCommandOptions("--no-execute-update-content-command=true");
    assertFalse(command.options.executeUpdateContentCommand, "executeUpdateContentCommand");
  }

  @Test
  public void executeUpdateContentCommandNegatedFalse() {
    final var command = parseCommandOptions("--no-execute-update-content-command=false");
    assertTrue(command.options.executeUpdateContentCommand, "executeUpdateContentCommand");
  }

  @Test
  public void executePostCreateCommand() {
    final var command = parseCommandOptions("--execute-post-create-command");
    assertTrue(command.options.executePostCreateCommand, "executePostCreateCommand");
  }

  @Test
  public void executePostCreateCommandTrue() {
    final var command = parseCommandOptions("--execute-post-create-command=true");
    assertTrue(command.options.executePostCreateCommand, "executePostCreateCommand");
  }

  @Test
  public void executePostCreateCommandFalse() {
    final var command = parseCommandOptions("--execute-post-create-command=false");
    assertFalse(command.options.executePostCreateCommand, "executePostCreateCommand");
  }

  @Test
  public void executePostCreateCommandNegated() {
    final var command = parseCommandOptions("--no-execute-post-create-command");
    assertFalse(command.options.executePostCreateCommand, "executePostCreateCommand");
  }

  @Test
  public void executePostCreateCommandNegatedTrue() {
    final var command = parseCommandOptions("--no-execute-post-create-command=true");
    assertFalse(command.options.executePostCreateCommand, "executePostCreateCommand");
  }

  @Test
  public void executePostCreateCommandNegatedFalse() {
    final var command = parseCommandOptions("--no-execute-post-create-command=false");
    assertTrue(command.options.executePostCreateCommand, "executePostCreateCommand");
  }

  @Test
  public void executePostStartCommand() {
    final var command = parseCommandOptions("--execute-post-start-command");
    assertTrue(command.options.executePostStartCommand, "executePostCreateCommand");
  }

  @Test
  public void executePostStartCommandTrue() {
    final var command = parseCommandOptions("--execute-post-start-command=true");
    assertTrue(command.options.executePostStartCommand, "executePostCreateCommand");
  }

  @Test
  public void executePostStartCommandFalse() {
    final var command = parseCommandOptions("--execute-post-start-command=false");
    assertFalse(command.options.executePostStartCommand, "executePostCreateCommand");
  }

  @Test
  public void executePostStartCommandNegated() {
    final var command = parseCommandOptions("--no-execute-post-start-command");
    assertFalse(command.options.executePostStartCommand, "executePostCreateCommand");
  }

  @Test
  public void executePostStartCommandNegatedTrue() {
    final var command = parseCommandOptions("--no-execute-post-start-command=true");
    assertFalse(command.options.executePostStartCommand, "executePostCreateCommand");
  }

  @Test
  public void executePostStartCommandNegatedFalse() {
    final var command = parseCommandOptions("--no-execute-post-start-command=false");
    assertTrue(command.options.executePostStartCommand, "executePostCreateCommand");
  }

  @Test
  public void executePostAttachCommand() {
    final var command = parseCommandOptions("--execute-post-attach-command");
    assertTrue(command.options.executePostAttachCommand, "executePostAttachCommand");
  }

  @Test
  public void executePostAttachCommandTrue() {
    final var command = parseCommandOptions("--execute-post-attach-command=true");
    assertTrue(command.options.executePostAttachCommand, "executePostAttachCommand");
  }

  @Test
  public void executePostAttachCommandFalse() {
    final var command = parseCommandOptions("--execute-post-attach-command=false");
    assertFalse(command.options.executePostAttachCommand, "executePostAttachCommand");
  }

  @Test
  public void executePostAttachCommandNegated() {
    final var command = parseCommandOptions("--no-execute-post-attach-command");
    assertFalse(command.options.executePostAttachCommand, "executePostAttachCommand");
  }

  @Test
  public void executePostAttachCommandNegatedTrue() {
    final var command = parseCommandOptions("--no-execute-post-attach-command=true");
    assertFalse(command.options.executePostAttachCommand, "executePostAttachCommand");
  }

  @Test
  public void executePostAttachCommandNegatedFalse() {
    final var command = parseCommandOptions("--no-execute-post-attach-command=false");
    assertTrue(command.options.executePostAttachCommand, "executePostAttachCommand");
  }

  private static TestCommand parseCommandOptions(final String... args) {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs(args);
    return command;
  }

}
