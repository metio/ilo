/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.version;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VersionTest {

  @Test
  void shouldDefineVersion() {
    Assertions.assertNotNull(Version.VERSION);
  }

  @Test
  void shouldCreateInstance() {
    Assertions.assertNotNull(new Version());
  }

}
