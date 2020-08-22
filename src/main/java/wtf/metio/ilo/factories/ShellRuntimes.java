/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.factories;

import wtf.metio.ilo.model.ShellCLI;
import wtf.metio.ilo.tools.Docker;
import wtf.metio.ilo.tools.LXD;
import wtf.metio.ilo.tools.Podman;

import java.util.List;

// TODO: move to shell package?
public final class ShellRuntimes {

  public static List<ShellCLI> allRuntimes() {
    final var podman = new Podman();
    final var docker = new Docker();
    final var lxd = new LXD();
    return List.of(podman, docker, lxd);
  }

  private ShellRuntimes() {
    // factory class
  }

}
