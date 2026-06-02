/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.compose;

import org.junit.jupiter.api.DisplayName;
import wtf.metio.ilo.test.CliToolTCK;

@DisplayName("DockerCompose")
class DockerComposeTest extends CliToolTCK<ComposeOptions, ComposeCLI> {

  @Override
  public ComposeCLI tool() {
    return new DockerCompose();
  }

  @Override
  protected ComposeOptions options() {
    return new ComposeOptions();
  }

  @Override
  protected String name() {
    return "docker-compose";
  }

}
