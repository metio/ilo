/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
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
