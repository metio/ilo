/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import wtf.metio.ilo.model.Matcher;
import wtf.metio.ilo.utils.Runtimes;

import java.util.Arrays;

public enum ComposeRuntime implements Matcher {

  PODMAN_COMPOSE("podman-compose", "pc"),
  PODS_COMPOSE("pods-compose", "pods"),
  FOOTLOOSE("footloose", "fl"),
  VAGRANT("vagrant", "v"),
  DOCKER_COMPOSE("docker-compose", "dc");

  private final String[] aliases;

  ComposeRuntime(final String... aliases) {
    this.aliases = aliases;
  }

  public static ComposeRuntime fromAlias(final String alias) {
    return Runtimes.firstMatching(alias, ComposeRuntime.values());
  }

  @Override
  public boolean matches(final String candidate) {
    return Arrays.stream(aliases).anyMatch(candidate::equalsIgnoreCase);
  }

  @Override
  public String toString() {
    return aliases[0];
  }

}
