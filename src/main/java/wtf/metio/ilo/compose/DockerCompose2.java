/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.compose;

/**
 * Support for 'docker compose' aka 'Compose V2'
 *
 * @see <a href="https://docs.docker.com/compose/cli-command/">official documentation</a>
 */
public final class DockerCompose2 extends DockerComposeLike {

  @Override
  public String name() {
    return "docker";
  }

  @Override
  public String command() {
    return "compose";
  }

}
