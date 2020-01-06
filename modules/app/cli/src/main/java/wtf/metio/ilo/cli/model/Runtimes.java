/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.cli.model;

import java.util.Arrays;

public enum Runtimes {

  PODMAN("podman", "p"),

  DOCKER("docker", "d"),

  PODMAN_COMPOSE("podmman-compose", "pc"),

  DOCKER_COMPOSE("docker-compose", "dc");

  private final String[] aliases;

  Runtimes(final String... aliases) {
    this.aliases = aliases;
  }

  public static Runtimes fromAlias(final String alias) {
    return Arrays.stream(Runtimes.values())
        .filter(runtime -> Arrays.stream(runtime.aliases).anyMatch(entry -> entry.equalsIgnoreCase(alias)))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

  public boolean matches(final String candidate) {
    return Arrays.stream(aliases)
        .anyMatch(candidate::equalsIgnoreCase);
  }

}
