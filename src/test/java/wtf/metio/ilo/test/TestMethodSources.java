/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.test;

import wtf.metio.ilo.compose.ComposeRuntime;
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

  private static Stream<String> composeRuntimes() {
    return Arrays.stream(ComposeRuntime.values())
      .flatMap(runtime -> Arrays.stream(runtime.aliases()));
  }

  private static Stream<String> dockerComposeLikeRuntimes() {
    return Stream.of(ComposeRuntime.DOCKER_COMPOSE, ComposeRuntime.PODMAN_COMPOSE, ComposeRuntime.DOCKER)
      .flatMap(runtime -> Arrays.stream(runtime.aliases()));
  }

}
