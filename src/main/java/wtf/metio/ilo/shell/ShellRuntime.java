/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.model.Matcher;
import wtf.metio.ilo.utils.Runtimes;

import java.util.Arrays;

public enum ShellRuntime implements Matcher {

  PODMAN("podman", "p"),
  LXD("lxd", "l"),
  DOCKER("docker", "d");

  private final String[] aliases;

  ShellRuntime(final String... aliases) {
    this.aliases = aliases;
  }

  public static ShellRuntime fromAlias(final String alias) {
    return Runtimes.firstMatching(alias, ShellRuntime.values());
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
