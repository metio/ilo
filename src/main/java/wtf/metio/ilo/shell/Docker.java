/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import java.util.List;

public final class Docker extends DockerLike {

  @Override
  public String name() {
    return "docker";
  }

  // Docker, unlike podman and nerdctl, has a 'dead' container state, so it is swept here too.
  @Override
  List<String> staleStatuses() {
    return List.of("created", "exited", "paused", "dead");
  }

}
