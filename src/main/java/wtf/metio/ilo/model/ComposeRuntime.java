/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.model;

import java.util.Arrays;

public enum ComposeRuntime {

  PODMAN_COMPOSE("podman-compose", "pc"),

  DOCKER_COMPOSE("docker-compose", "dc");

  private final String[] aliases;

  ComposeRuntime(final String... aliases) {
    this.aliases = aliases;
  }

  public static ComposeRuntime fromAlias(final String alias) {
    return Arrays.stream(ComposeRuntime.values())
        .filter(runtime -> runtime.matches(alias))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

  public boolean matches(final String candidate) {
    return Arrays.stream(aliases).anyMatch(candidate::equalsIgnoreCase);
  }

  @Override
  public String toString() {
    return aliases[0];
  }

}
