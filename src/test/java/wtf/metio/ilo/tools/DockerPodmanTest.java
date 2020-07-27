/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.ilo.shell.ShellRuntime;
import wtf.metio.ilo.test.TestSources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("DockerPodman")
class DockerPodmanTest extends TestSources {

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("generate pull arguments")
  void pullArguments(final String tool) {
    final var runtime = ShellRuntime.fromAlias(tool);
    final var options = new ShellOptions();
    options.runtime = runtime;
    options.pull = true;
    options.image = "example:test";
    final var arguments = DockerPodman.pullArguments(options, runtime.toString());
    assertEquals(String.format("%s pull example:test", runtime), String.join(" ", arguments));
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("generate build arguments")
  void buildArguments(final String tool) {
    final var runtime = ShellRuntime.fromAlias(tool);
    final var options = new ShellOptions();
    options.runtime = runtime;
    options.pull = true;
    options.image = "example:test";
    options.dockerfile = "Dockerfile";
    final var arguments = DockerPodman.buildArguments(options, runtime.toString());
    assertEquals(String.format("%s build --tag example:test --file Dockerfile .", runtime), String.join(" ", arguments));
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("generate run arguments")
  void runArguments(final String tool) {
    final var runtime = ShellRuntime.fromAlias(tool);
    final var options = new ShellOptions();
    options.runtime = runtime;
    options.pull = true;
    options.image = "example:test";
    options.dockerfile = "Dockerfile";
    final var arguments = DockerPodman.runArguments(options, runtime.toString());
    assertEquals(String.format("%s run --rm example:test", runtime), String.join(" ", arguments));
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("generate clean arguments")
  void cleanArguments(final String tool) {
    final var runtime = ShellRuntime.fromAlias(tool);
    final var options = new ShellOptions();
    options.runtime = runtime;
    options.pull = true;
    options.image = "example:test";
    options.dockerfile = "Dockerfile";
    options.removeImage = true;
    final var arguments = DockerPodman.cleanupArguments(options, runtime.toString());
    assertEquals(String.format("%s rmi example:test", runtime), String.join(" ", arguments));
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("interactive run")
  void interactive(final String tool) {
    final var runtime = ShellRuntime.fromAlias(tool);
    final var options = new ShellOptions();
    options.runtime = runtime;
    options.pull = true;
    options.image = "example:test";
    options.dockerfile = "Dockerfile";
    options.interactive = true;
    final var arguments = DockerPodman.runArguments(options, runtime.toString());
    assertEquals(String.format("%s run --rm --interactive --tty example:test", runtime), String.join(" ", arguments));
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("custom command run")
  void mount(final String tool) {
    final var runtime = ShellRuntime.fromAlias(tool);
    final var options = new ShellOptions();
    options.runtime = runtime;
    options.pull = true;
    options.dockerfile = "Dockerfile";
    options.commands = List.of("example:test", "/bin/bash", "-c", "whoami");
    final var arguments = DockerPodman.runArguments(options, runtime.toString());
    assertEquals(String.format("%s run --rm example:test /bin/bash -c whoami", runtime), String.join(" ", arguments));
  }

  @ParameterizedTest
  @MethodSource("dockerLikeRuntimes")
  @DisplayName("pass-through runtime options")
  void runtimeOptions(final String tool) {
    final var runtime = ShellRuntime.fromAlias(tool);
    final var options = new ShellOptions();
    options.runtime = runtime;
    options.pull = true;
    options.dockerfile = "Dockerfile";
    options.commands = List.of("--volume=/abc/123:/abc:Z", "example:test", "/bin/bash", "-c", "whoami");
    final var arguments = DockerPodman.runArguments(options, runtime.toString());
    assertEquals(String.format("%s run --rm --volume=/abc/123:/abc:Z example:test /bin/bash -c whoami", runtime), String.join(" ", arguments));
  }

}
