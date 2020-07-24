/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.options.ShellOptions;

import java.util.List;

public final class Podman implements ShellCLI {

  @Override
  public String name() {
    return "podman";
  }

  @Override
  public List<String> arguments(final ShellOptions options) {
    return DockerPodman.arguments(options, name());
  }

}
