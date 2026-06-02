/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.compose;

public final class PodmanCompose extends DockerComposeLike {

  @Override
  public String name() {
    return "podman-compose";
  }

}
