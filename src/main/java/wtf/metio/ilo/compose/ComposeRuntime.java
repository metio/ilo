/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
    // A forced (or ILO_COMPOSE_RUNTIME) choice must exist, or the run fails — never silently fall back
    // to a different runtime than the user asked for. 'exists()' is checked exactly once per path here
    // (the docker-compose-v2 probe spawns a subprocess, so the old trailing re-check was wasteful).
    final var forced = Optional.ofNullable(preferred)
        .or(() -> Runtime.fromEnvironment(EnvironmentVariables.ILO_COMPOSE_RUNTIME.name(), values()))
        .map(ComposeRuntime::cli);
    if (forced.isPresent()) {
      return forced
          .filter(CliTool::exists)
          .orElseThrow(() -> new NoMatchingRuntimeException("The selected compose runtime '"
              + forced.get().name() + "' is not available on your system. Install it (for 'docker' that "
              + "means the Docker Compose v2 plugin), or choose another with --runtime / ILO_COMPOSE_RUNTIME."));
    }
    return Arrays.stream(ComposeRuntime.values())
        .map(ComposeRuntime::cli)
        .filter(CliTool::exists)
        .findFirst()
        .orElseThrow(NoMatchingRuntimeException::new);
  }

}
