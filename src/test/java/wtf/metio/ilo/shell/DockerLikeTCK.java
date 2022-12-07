/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import wtf.metio.ilo.test.CliToolTCK;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SystemStubsExtension.class)
abstract class DockerLikeTCK extends CliToolTCK<ShellOptions, ShellCLI> {

  @Test
  @DisplayName("generate pull arguments")
  void pullArguments() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.pull = true;
    options.image = "example:test";
    final var arguments = tool().pullArguments(options);
    assertEquals(String.format("%s pull example:test", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("generate build arguments")
  void buildArguments() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.image = "example:test";
    options.containerfile = "Dockerfile";
    final var arguments = tool().buildArguments(options);
    assertEquals(String.format("%s build --file Dockerfile --tag example:test", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("generate build arguments with pull")
  void buildArgumentsWithPull() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.pull = true;
    options.image = "example:test";
    options.containerfile = "Dockerfile";
    final var arguments = tool().buildArguments(options);
    assertEquals(String.format("%s build --file Dockerfile --pull --tag example:test", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("generate run arguments")
  void runArguments() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.image = "example:test";
    options.workingDir = "some/dir";
    final var arguments = tool().runArguments(options);
    assertEquals(String.format("%s run --rm --workdir %s example:test", name(), options.workingDir), String.join(" ", arguments));
  }

  @Test
  @DisplayName("generate clean arguments")
  void cleanArguments() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.image = "example:test";
    options.removeImage = true;
    final var arguments = tool().cleanupArguments(options);
    assertEquals(String.format("%s rmi example:test", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("interactive run")
  void interactive() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.image = "example:test";
    options.interactive = true;
    options.workingDir = "some/dir";
    final var arguments = tool().runArguments(options);
    assertEquals(String.format("%s run --rm --workdir %s --interactive --tty example:test", name(), options.workingDir), String.join(" ", arguments));
  }

  @Test
  @DisplayName("hostname run")
  void hostname() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.image = "example:test";
    options.hostname = "some-test";
    options.workingDir = "some/dir";
    final var arguments = tool().runArguments(options);
    assertEquals(String.format("%s run --rm --workdir %s --hostname some-test example:test", name(), options.workingDir), String.join(" ", arguments));
  }

  @Test
  @DisplayName("custom command run")
  void customCommand() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.commands = List.of("example:test", "/bin/bash", "-c", "whoami");
    options.workingDir = "some/dir";
    final var arguments = tool().runArguments(options);
    assertEquals(String.format("%s run --rm --workdir %s example:test /bin/bash -c whoami", name(), options.workingDir), String.join(" ", arguments));
  }

  @Test
  @DisplayName("custom build context")
  void context() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.context = ".";
    options.image = "example:test";
    options.containerfile = "Dockerfile";
    final var arguments = tool().buildArguments(options);
    assertEquals(String.format("%s build --file Dockerfile --tag example:test .", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("pass-through runtime options")
  void runtimeOptions() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.workingDir = "some/dir";
    options.commands = List.of("--volume=/abc/123:/abc:z", "example:test", "/bin/bash", "-c", "whoami");
    final var arguments = tool().runArguments(options);
    assertEquals(String.format("%s run --rm --workdir %s --volume=/abc/123:/abc:z example:test /bin/bash -c whoami", name(), options.workingDir), String.join(" ", arguments));
  }

  @Test
  @DisplayName("custom env variables")
  void variables() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.image = "example:test";
    options.variables = List.of("KEY=value", "OTHER=value");
    options.workingDir = "some/dir";
    final var arguments = tool().runArguments(options);
    assertEquals(String.format("%s run --rm --workdir %s --env KEY=value --env OTHER=value example:test", name(), options.workingDir), String.join(" ", arguments));
  }

  @Test
  @DisplayName("runs as specific user")
  void runAs() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.image = "example:test";
    options.runAs = "1234:5678";
    final var arguments = tool().runArguments(options);
    final var commandLine = String.join(" ", arguments);
    assertAll("command line",
        () -> assertTrue(commandLine.startsWith(String.format("%s run --rm --user 1234:5678", name())), "parameters"),
        () -> assertTrue(commandLine.contains("example:test"), "image missing"),
        () -> assertTrue(commandLine.contains("ilo"), "ilo"),
        () -> assertTrue(commandLine.contains(".passwd"), "passwd"));
  }

}
