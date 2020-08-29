/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.model.CliExecutor;

import java.util.ArrayList;
import java.util.List;

class TestShellExecutor implements CliExecutor<ShellRuntime, ShellCLI> {

  private final List<List<String>> collectedArguments = new ArrayList<>(4);

  @Override
  public ShellCLI selectRuntime(final ShellRuntime runtime) {
    return ShellRuntimes.allRuntimes().stream()
        .filter(cli -> runtime.matches(cli.name()))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

  @Override
  public int execute(final List<String> arguments, final boolean debug) {
    collectedArguments.add(arguments);
    return 0;
  }

  public List<String> pullArguments() {
    return collectedArguments.get(0);
  }

  public List<String> buildArguments() {
    return collectedArguments.get(1);
  }

  public List<String> runArguments() {
    return collectedArguments.get(2);
  }

  public List<String> cleanupArguments() {
    return collectedArguments.get(3);
  }

}
