/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.version;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VersionProvider")
class VersionProviderTest {

  @Test
  @DisplayName("provides ilo/jvm/os infos")
  void getVersion() {
    // given
    final var provider = new VersionProvider();

    // when
    final var version = provider.getVersion();

    // then
    assertAll("versions",
      () -> assertEquals(3, version.length, "length"),
      () -> assertTrue(version[0].contains("ilo:"), "ilo"),
      () -> assertTrue(version[1].contains("JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})"), "JVM"),
      () -> assertTrue(version[2].contains("OS:  ${os.name} ${os.version} ${os.arch}"), "OS"));
  }

}
