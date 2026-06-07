/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.devcontainer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;
import wtf.metio.ilo.compose.ComposeRuntime;
import wtf.metio.ilo.shell.ShellRuntime;
import wtf.metio.ilo.test.TestMethodSources;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class DevcontainerOptionsParserTest extends TestMethodSources {

  @CommandLine.Command
  static class TestCommand implements Runnable {

    @CommandLine.Mixin
    public DevcontainerOptions options;

    @Override
    public void run() {
      // never executed: this command exists only so picocli can bind and parse the options
    }

  }

  @Test
  void defaultValues() {
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

  // A non-negatable boolean flag (default off): present or '=true' turns it on, '=false' off.
  @ParameterizedTest(name = "--{0}")
  @MethodSource("booleanFlags")
  void booleanFlag(final String flag, final Function<DevcontainerOptions, Boolean> getter) {
    assertAll(
        () -> assertTrue(getter.apply(parseCommandOptions("--" + flag).options), "--" + flag),
        () -> assertTrue(getter.apply(parseCommandOptions("--" + flag + "=true").options), "--" + flag + "=true"),
        () -> assertFalse(getter.apply(parseCommandOptions("--" + flag + "=false").options), "--" + flag + "=false")
    );
  }

  static Stream<Arguments> booleanFlags() {
    return Stream.of(
        arguments("debug", (Function<DevcontainerOptions, Boolean>) options -> options.debug),
        arguments("pull", (Function<DevcontainerOptions, Boolean>) options -> options.pull),
        arguments("remove-image", (Function<DevcontainerOptions, Boolean>) options -> options.removeImage)
    );
  }

  // A negatable boolean flag (default on): the '--flag'/'--flag=true|false' forms set it directly,
  // and the '--no-flag' forms invert that, so '--no-flag=false' leaves it on.
  @ParameterizedTest(name = "--{0}")
  @MethodSource("negatableBooleanFlags")
  void negatableBooleanFlag(final String flag, final Function<DevcontainerOptions, Boolean> getter) {
    assertAll(
        () -> assertTrue(getter.apply(parseCommandOptions("--" + flag).options), "--" + flag),
        () -> assertTrue(getter.apply(parseCommandOptions("--" + flag + "=true").options), "--" + flag + "=true"),
        () -> assertFalse(getter.apply(parseCommandOptions("--" + flag + "=false").options), "--" + flag + "=false"),
        () -> assertFalse(getter.apply(parseCommandOptions("--no-" + flag).options), "--no-" + flag),
        () -> assertFalse(getter.apply(parseCommandOptions("--no-" + flag + "=true").options), "--no-" + flag + "=true"),
        () -> assertTrue(getter.apply(parseCommandOptions("--no-" + flag + "=false").options), "--no-" + flag + "=false")
    );
  }

  static Stream<Arguments> negatableBooleanFlags() {
    return Stream.of(
        arguments("mount-project-dir", (Function<DevcontainerOptions, Boolean>) options -> options.mountProjectDir),
        arguments("execute-initialize-command", (Function<DevcontainerOptions, Boolean>) options -> options.executeInitializeCommand),
        arguments("execute-on-create-command", (Function<DevcontainerOptions, Boolean>) options -> options.executeOnCreateCommand),
        arguments("execute-update-content-command", (Function<DevcontainerOptions, Boolean>) options -> options.executeUpdateContentCommand),
        arguments("execute-post-create-command", (Function<DevcontainerOptions, Boolean>) options -> options.executePostCreateCommand),
        arguments("execute-post-start-command", (Function<DevcontainerOptions, Boolean>) options -> options.executePostStartCommand),
        arguments("execute-post-attach-command", (Function<DevcontainerOptions, Boolean>) options -> options.executePostAttachCommand)
    );
  }

  @Test
  void locations() {
    final var command = parseCommandOptions("test.json", "another.txt");
    assertIterableEquals(List.of("test.json", "another.txt"), command.options.locations, "locations");
  }

  private static TestCommand parseCommandOptions(final String... args) {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs(args);
    return command;
  }

}
