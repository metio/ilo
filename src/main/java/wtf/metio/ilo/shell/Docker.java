/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import java.util.List;
import java.util.function.Function;

public final class Docker extends DockerLike {

  @Override
  public String name() {
    return "docker";
  }

  // Docker has no user namespace by default, so unlike rootless podman/nerdctl it cannot map a user
  // into the host: a rootful daemon writes files as the container user's literal UID. Whether that
  // needs remapping depends on the daemon being rootful, so the daemon is probed first.
  @Override
  public RemoteUserMapping remoteUserMapping(final boolean enabled, final String remoteUser,
      final Function<List<String>, String> capture) {
    if (!enabled) {
      return RemoteUserMapping.NONE;
    }
    return RemoteUserMapping.resolve(true, isRootfulDaemon(capture), remoteUser, enabled);
  }

  // 'docker info' lists the daemon's security options; rootless mode adds a 'name=rootless' entry,
  // while a rootful daemon lists other 'name=' options without it. A response carrying no 'name='
  // option at all is not a Docker daemon answering — the podman 'docker' shim has no such field and
  // an unreachable daemon returns nothing — so the daemon is not treated as rootful.
  private boolean isRootfulDaemon(final Function<List<String>, String> capture) {
    final var securityOptions = capture.apply(List.of(name(), "info", "--format", "{{.SecurityOptions}}"));
    return securityOptions.contains("name=") && !securityOptions.contains("rootless");
  }

  // Docker, unlike podman and nerdctl, has a 'dead' container state, so it is swept here too.
  @Override
  List<String> staleStatuses() {
    return List.of("created", "exited", "paused", "dead");
  }

}
