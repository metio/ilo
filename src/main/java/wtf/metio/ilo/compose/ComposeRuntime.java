/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import wtf.metio.ilo.cli.EnvironmentVariables;
import wtf.metio.ilo.errors.NoMatchingRuntimeException;
import wtf.metio.ilo.model.CliTool;
import wtf.metio.ilo.model.Runtime;

import java.util.Arrays;
import java.util.Optional;

public enum ComposeRuntime implements Runtime<ComposeCLI> {

  DOCKER(new DockerCompose2(), "docker", "d"),
  DOCKER_COMPOSE(new DockerCompose(), "docker-compose", "dc"),
  PODMAN_COMPOSE(new PodmanCompose(), "podman-compose", "pc");

  private final ComposeCLI cli;
  private final String[] aliases;

  ComposeRuntime(final ComposeCLI cli, final String... aliases) {
    this.cli = cli;
    this.aliases = aliases;
  }

  public static ComposeRuntime fromAlias(final String alias) {
    return Runtime.firstMatching(alias, values());
  }

  @Override
  public String[] aliases() {
    return aliases;
  }

  @Override
  public ComposeCLI cli() {
    return cli;
  }

  /**
   * Select a runtime for 'ilo compose'.
   *
   * @param preferred The runtime to force, or null for auto-selection.
   * @return The selected compose runtime.
   */
  public static ComposeCLI autoSelect(final ComposeRuntime preferred) {
    return Optional.ofNullable(preferred)
        .or(() -> Optional.ofNullable(System.getenv(EnvironmentVariables.ILO_COMPOSE_RUNTIME.name()))
            .map(ComposeRuntime::fromAlias))
        .map(ComposeRuntime::cli)
        .or(() -> Arrays.stream(ComposeRuntime.values())
            .map(ComposeRuntime::cli)
            .filter(CliTool::exists)
            .findFirst())
        .filter(CliTool::exists)
        .orElseThrow(NoMatchingRuntimeException::new);
  }

}
