/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.compose;

import wtf.metio.ilo.cli.Executables;

/**
 * Support for 'docker compose' aka 'Compose V2'
 *
 * @see <a href="https://docs.docker.com/compose/cli-command/">official documentation</a>
 */
public final class DockerCompose2 extends DockerComposeLike {

  @Override
  public String name() {
    return "docker";
  }

  @Override
  public String command() {
    return "compose";
  }

  @Override
  public boolean exists() {
    // The 'docker' binary alone is not enough — the Compose V2 plugin must be installed, or every
    // 'docker compose …' fails. Probing 'docker compose version' lets auto-selection fall through to
    // docker-compose (V1) or podman-compose instead of hard-failing with "compose is not a docker
    // command". The probe is best-effort: the plugin writes nothing to stdout when missing, and a
    // 'docker' whose daemon hangs (so 'docker compose version' never returns) reads as absent rather
    // than stalling the run — or, in tests, aborting it with a timeout error.
    return super.exists() && !Executables.probeOutput(name(), command(), "version").isBlank();
  }

}
