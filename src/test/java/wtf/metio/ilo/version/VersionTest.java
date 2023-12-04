/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
