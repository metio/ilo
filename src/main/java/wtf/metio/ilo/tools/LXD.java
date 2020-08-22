/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.model.ShellCLI;
import wtf.metio.ilo.shell.ShellOptions;

import java.util.List;

public final class LXD implements ShellCLI {

  @Override
  public String name() {
    return "lxc";
  }

  @Override
  public List<String> pullArguments(final ShellOptions options) {
    return List.of();
  }

  @Override
  public List<String> buildArguments(final ShellOptions options) {
    return List.of();
  }

  @Override
  public List<String> runArguments(final ShellOptions options) {
    final var currentDir = System.getProperty("user.dir");
    return List.of(name(), "launch", options.image, currentDir);
  }

  @Override
  public List<String> cleanupArguments(final ShellOptions options) {
    return List.of();
  }

}
