/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.compose;

public final class DockerCompose extends DockerComposeLike {

  @Override
  public String name() {
    return "docker-compose";
  }

}
