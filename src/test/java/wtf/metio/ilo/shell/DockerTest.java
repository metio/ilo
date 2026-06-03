/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;

import java.util.List;

@DisplayName("Docker")
class DockerTest extends DockerLikeTCK {

  @Override
  public ShellCLI tool() {
    return new Docker();
  }

  @Override
  protected ShellOptions options() {
    return new ShellOptions();
  }

  @Override
  protected String name() {
    return "docker";
  }

  @Override
  protected List<String> staleStatuses() {
    return List.of("created", "exited", "paused", "dead");
  }

}
