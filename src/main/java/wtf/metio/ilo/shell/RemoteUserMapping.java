/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import wtf.metio.ilo.os.OSSupport;

import java.util.List;
import java.util.stream.Stream;

/**
 * How a runtime is asked to align the container user with the host user so files written into the
 * mounted project stay owned by the caller. One user-facing switch ({@code --update-remote-user-uid})
 * selects between these per the runtime and the container user, mirroring the devcontainer spec's
 * {@code updateRemoteUserUID}.
 */
enum RemoteUserMapping {

  /**
   * Nothing to do: the runtime already maps the user to the host (rootless runtimes running as root),
   * or the feature is disabled.
   */
  NONE,

  /**
   * Rootless podman/nerdctl map the container's root to the host user automatically, but a non-root
   * user lands under a sub-UID; a keep-id user namespace maps the host user into the container so its
   * files stay owned by the caller.
   */
  KEEP_ID,

  /**
   * Rootful Docker has no user namespace and no non-root user to remap, so the container is run as the
   * bare host UID and GID. The exec repeats it because an exec otherwise defaults to root.
   */
  HOST_USER,

  /**
   * Rootful Docker with a non-root user: a derived image remaps that user's UID and GID to the host's,
   * and the container runs as that user, keeping its name, home, and passwd entry.
   */
  REMAP;

  // '--user' runs the container or exec as the given user, as 'name', 'uid', or 'uid:gid'.
  private static final String USER = "--user";

  /**
   * Picks the mapping for a runtime and container user. The {@code rootfulDocker} flag is only
   * consulted for Docker; podman and nerdctl are always treated as their rootless selves.
   *
   * @param docker        Whether the runtime is Docker (which has no default user namespace).
   * @param rootfulDocker Whether the Docker daemon is rootful (ignored unless {@code docker}).
   * @param remoteUser    The container user, or {@code null}/{@code root}/{@code 0} for the root user.
   * @param enabled       Whether {@code updateRemoteUserUID} is in effect.
   * @return The mapping to apply.
   */
  static RemoteUserMapping resolve(final boolean docker, final boolean rootfulDocker,
      final String remoteUser, final boolean enabled) {
    if (!enabled) {
      return NONE;
    }
    if (docker) {
      if (!rootfulDocker) {
        return NONE;
      }
      return isRoot(remoteUser) ? HOST_USER : REMAP;
    }
    return isRoot(remoteUser) ? NONE : KEEP_ID;
  }

  /**
   * The {@code create}-time arguments this mapping contributes. {@link #REMAP} runs the container as
   * the remapped user by name; the derived image that performs the remap is assembled separately.
   *
   * @param remoteUser The container user.
   * @param expand     Expands host-shell expressions such as {@code $(id -u)}.
   * @return The arguments to add to the run command.
   */
  List<String> createArguments(final String remoteUser, final String remoteUid, final String remoteGid,
      final OSSupport.Expander expand) {
    return switch (this) {
      case NONE, REMAP -> runAs(remoteUser);
      case KEEP_ID -> Stream.concat(Stream.of(keepId(remoteUid, remoteGid)), runAs(remoteUser).stream()).toList();
      case HOST_USER -> hostUserArguments(expand.expand("$(id -u):$(id -g)"));
    };
  }

  // Pins the keep-id namespace to the container user's own UID/GID so the host user maps onto that user
  // regardless of whether the host UID matches it. Without the user's ids a plain keep-id is used, which
  // only aligns a container user whose UID already equals the host UID.
  private static String keepId(final String remoteUid, final String remoteGid) {
    if (remoteUid == null || remoteGid == null) {
      return "--userns=keep-id";
    }
    return "--userns=keep-id:uid=" + remoteUid + ",gid=" + remoteGid;
  }

  /**
   * The {@code exec}-time arguments this mapping contributes. Podman/nerdctl set the mapping on the
   * container, so an exec inherits it and needs nothing; Docker's exec otherwise defaults to root, so
   * the user is requested again.
   *
   * @param remoteUser The container user.
   * @param expand     Expands host-shell expressions such as {@code $(id -u)}.
   * @return The arguments to add to the exec command.
   */
  List<String> execArguments(final String remoteUser, final OSSupport.Expander expand) {
    return switch (this) {
      // A keep-id exec inherits the container's user namespace and user, so it needs nothing. For the
      // others the named user is repeated, because a Docker exec otherwise defaults to root; doing so
      // on podman/nerdctl too is harmless, since it just names the user the exec already inherits.
      case KEEP_ID -> List.of();
      case NONE, REMAP -> runAs(remoteUser);
      case HOST_USER -> hostUserArguments(expand.expand("$(id -u):$(id -g)"));
    };
  }

  // Runs the container as the bare host UID:GID. The ids come from the host shell ('$(id -u):$(id -g)');
  // if that yields anything other than 'digits:digits' — 'id' is unavailable, or no shell is present so
  // the expression comes back verbatim — the mapping degrades to letting the runtime pick the user, with
  // a warning, rather than passing a malformed '--user' that the runtime would reject.
  // visible for testing
  static List<String> hostUserArguments(final String hostUserAndGroup) {
    if (null != hostUserAndGroup && hostUserAndGroup.matches("\\d+:\\d+")) {
      return List.of(USER, hostUserAndGroup);
    }
    System.err.println("ilo could not determine the host user id (got '" + hostUserAndGroup
        + "'); the container runs as its default user, so files it creates may not be owned by you.");
    return List.of();
  }

  // Runs the container as a named user; the root user is left to the runtime (rootless runtimes map it
  // to the host) or, on rootful Docker, to {@link #HOST_USER}.
  private static List<String> runAs(final String remoteUser) {
    return isRoot(remoteUser) ? List.of() : List.of(USER, remoteUser);
  }

  private static boolean isRoot(final String remoteUser) {
    return remoteUser == null || remoteUser.isBlank() || remoteUser.equals("root") || remoteUser.equals("0");
  }

}
