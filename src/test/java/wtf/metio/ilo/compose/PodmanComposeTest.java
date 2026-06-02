/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.compose;

import org.junit.jupiter.api.DisplayName;
import wtf.metio.ilo.test.CliToolTCK;

@DisplayName("PodmanCompose")
class PodmanComposeTest extends CliToolTCK<ComposeOptions, ComposeCLI> {

  @Override
  public ComposeCLI tool() {
    return new PodmanCompose();
  }

  @Override
  protected ComposeOptions options() {
    return new ComposeOptions();
  }

  @Override
  protected String name() {
    return "podman-compose";
  }

}
