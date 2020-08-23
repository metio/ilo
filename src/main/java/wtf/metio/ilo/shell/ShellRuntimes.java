/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.model.ShellCLI;
import wtf.metio.ilo.tools.Docker;
import wtf.metio.ilo.tools.LXD;
import wtf.metio.ilo.tools.Podman;

import java.util.List;

public final class ShellRuntimes {

  public static List<ShellCLI> allRuntimes() {
    return List.of(new Podman(), new Docker(), new LXD());
  }

  private ShellRuntimes() {
    // factory class
  }

}
