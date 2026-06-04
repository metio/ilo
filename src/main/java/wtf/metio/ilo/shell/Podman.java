/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

public final class Podman extends DockerLike {

  @Override
  public String name() {
    return "podman";
  }

  // Podman accepts '--userns=keep-id:uid=…,gid=…' to pin the host user onto a specific container user.
  @Override
  public boolean supportsKeepIdUid() {
    return true;
  }

}
