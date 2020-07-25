/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.compose.ComposeCLI;

@DisplayName("PodsCompose")
class PodsComposeTest extends CLI_TOOL_TCK<ComposeCLI> {

  @Override
  public ComposeCLI tool() {
    return new PodsCompose();
  }

  @Test
  @DisplayName("has runtime name")
  void shouldHaveName() {
    assertName("pods-compose");
  }

}