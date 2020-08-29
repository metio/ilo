/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.model.ShellCLI;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.ilo.shell.ShellRuntime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class DockerPodmanTCK extends CLI_TOOL_TCK<ShellOptions, ShellCLI> {

  @Test
  @DisplayName("generate pull arguments")
  void pullArguments() {
    final var options = new ShellOptions();
    options.pull = true;
    options.image = "example:test";
    final var arguments = tool().pullArguments(options);
    assertEquals(String.format("%s pull example:test", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("generate build arguments")
  void buildArguments() {
    final var options = new ShellOptions();
    options.image = "example:test";
    options.dockerfile = "Dockerfile";
    final var arguments = tool().buildArguments(options);
    assertEquals(String.format("%s build --file Dockerfile --tag example:test .", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("generate build arguments with pull")
  void buildArgumentsWithPull() {
    final var options = new ShellOptions();
    options.pull = true;
    options.image = "example:test";
    options.dockerfile = "Dockerfile";
    final var arguments = tool().buildArguments(options);
    assertEquals(String.format("%s build --file Dockerfile --pull --tag example:test .", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("generate run arguments")
  void runArguments() {
    final var options = new ShellOptions();
    options.image = "example:test";
    final var arguments = tool().runArguments(options);
    assertEquals(String.format("%s run --rm example:test", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("generate clean arguments")
  void cleanArguments() {
    final var options = new ShellOptions();
    options.image = "example:test";
    options.removeImage = true;
    final var arguments = tool().cleanupArguments(options);
    assertEquals(String.format("%s rmi example:test", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("interactive run")
  void interactive() {
    final var options = new ShellOptions();
    options.image = "example:test";
    options.interactive = true;
    final var arguments = tool().runArguments(options);
    assertEquals(String.format("%s run --rm --interactive --tty example:test", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("custom command run")
  void mount() {
    final var options = new ShellOptions();
    options.commands = List.of("example:test", "/bin/bash", "-c", "whoami");
    final var arguments = tool().runArguments(options);
    assertEquals(String.format("%s run --rm example:test /bin/bash -c whoami", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("pass-through runtime options")
  void runtimeOptions() {
    final var options = new ShellOptions();
    options.commands = List.of("--volume=/abc/123:/abc:Z", "example:test", "/bin/bash", "-c", "whoami");
    final var arguments = tool().runArguments(options);
    assertEquals(String.format("%s run --rm --volume=/abc/123:/abc:Z example:test /bin/bash -c whoami", name()), String.join(" ", arguments));
  }

}
