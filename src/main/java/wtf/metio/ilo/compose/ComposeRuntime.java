/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import wtf.metio.ilo.model.Runtime;

public enum ComposeRuntime implements Runtime {

  DOCKER("docker", "d"),
  DOCKER_COMPOSE("docker-compose", "dc"),
  PODMAN_COMPOSE("podman-compose", "pc");

  private final String[] aliases;

  ComposeRuntime(final String... aliases) {
    this.aliases = aliases;
  }

  public static ComposeRuntime fromAlias(final String alias) {
    return Runtime.firstMatching(alias, values());
  }

  @Override
  public String[] aliases() {
    return aliases;
  }

}
