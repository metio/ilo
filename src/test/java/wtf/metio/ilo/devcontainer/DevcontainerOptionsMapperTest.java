/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static wtf.metio.ilo.devcontainer.DevcontainerOptionsMapper.composeOptions;
import static wtf.metio.ilo.devcontainer.DevcontainerOptionsMapper.shellOptions;

@DisplayName("DevcontainerOptionsMapper")
class DevcontainerOptionsMapperTest {

  @Nested
  @DisplayName("shell options")
  class ShellOptionsMapper {

    @Test
    void shouldReturnNonNullValues() {
      assertNotNull(shellOptions(new DevcontainerOptions(), new DevcontainerJson()));
    }

  }

  @Nested
  @DisplayName("compose options")
  class ComposeOptionsMapper {

    @Test
    void shouldReturnNonNullValues() {
      assertNotNull(composeOptions(new DevcontainerOptions(), new DevcontainerJson()));
    }

  }

}
