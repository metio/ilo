/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import wtf.metio.ilo.os.OSSupport;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class Docker extends DockerLike {

  @Override
  public String name() {
    return "docker";
  }

  // Rootful Docker runs the container as root with no user namespace, so files it writes into the
  // mounted project land owned by root on the host — a surprise the caller can avoid with
  // --current-user. Rootless Docker maps the container's root to the host user and so needs no hint.
  @Override
  public Optional<String> currentUserHint(final ShellOptions options, final Function<List<String>, String> capture) {
    if (options.currentUser || !isRootfulDaemon(capture)) {
      return Optional.empty();
    }
    return Optional.of("ilo: Docker writes files in the mounted project as root; pass --current-user "
        + "to keep them owned by you. See the File Ownership documentation.");
  }

  // 'docker info' lists the daemon's security options; rootless mode adds a 'name=rootless' entry,
  // while a rootful daemon lists other 'name=' options without it. A response carrying no 'name='
  // option at all is not a Docker daemon answering — the podman 'docker' shim has no such field and
  // an unreachable daemon returns nothing — so no claim about file ownership is made.
  private boolean isRootfulDaemon(final Function<List<String>, String> capture) {
    final var securityOptions = capture.apply(List.of(name(), "info", "--format", "{{.SecurityOptions}}"));
    return securityOptions.contains("name=") && !securityOptions.contains("rootless");
  }

  // Docker, unlike podman and nerdctl, has a 'dead' container state, so it is swept here too.
  @Override
  List<String> staleStatuses() {
    return List.of("created", "exited", "paused", "dead");
  }

  // Docker has no user namespace by default, so it cannot remap UIDs the way rootless podman/nerdctl
  // do. To keep files in the mounted project owned by the host user, the host UID:GID is requested
  // explicitly — on the run and on every exec, since exec otherwise defaults to root.
  @Override
  List<String> currentUserCreateArguments(final ShellOptions options, final OSSupport.Expander expand) {
    return hostUser(options, expand);
  }

  @Override
  List<String> currentUserExecArguments(final ShellOptions options, final OSSupport.Expander expand) {
    return hostUser(options, expand);
  }

  private static List<String> hostUser(final ShellOptions options, final OSSupport.Expander expand) {
    return options.currentUser ? List.of("--user", expand.expand("$(id -u):$(id -g)")) : List.of();
  }

}
