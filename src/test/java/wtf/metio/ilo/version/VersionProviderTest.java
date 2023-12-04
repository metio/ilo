/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
