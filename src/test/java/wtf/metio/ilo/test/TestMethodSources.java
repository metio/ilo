/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.test;

import wtf.metio.ilo.shell.ShellRuntime;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Base class that provides various method sources for parameterized tests.
 */
public abstract class TestMethodSources {

  private static Stream<String> shellRuntimes() {
    return Arrays.stream(ShellRuntime.values())
        .flatMap(runtime -> Arrays.stream(runtime.aliases()));
  }

  private static Stream<String> dockerLikeRuntimes() {
    return Stream.of(ShellRuntime.DOCKER, ShellRuntime.PODMAN, ShellRuntime.NERDCTL)
        .flatMap(runtime -> Arrays.stream(runtime.aliases()));
  }

}
