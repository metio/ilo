/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.model.Runtime;
import wtf.metio.ilo.utils.Runtimes;

public enum ShellRuntime implements Runtime {

  DOCKER("docker", "d"),
  LXD("lxd", "l"),
  PODMAN("podman", "p");

  private final String[] aliases;

  ShellRuntime(final String... aliases) {
    this.aliases = aliases;
  }

  public static ShellRuntime fromAlias(final String alias) {
    return Runtimes.firstMatching(alias, values());
  }

  @Override
  public String[] aliases() {
    return aliases;
  }

}
