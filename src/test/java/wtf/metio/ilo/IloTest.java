/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static wtf.metio.ilo.test.TestResources.testResources;

@DisplayName("Ilo")
@ExtendWith(SystemStubsExtension.class)
class IloTest {

  @Test
  @DisplayName("reads .rc files")
  void supportsRunCommands(final SystemProperties properties) {
    properties.set("user.dir", testResources(Ilo.class).resolve("root").toAbsolutePath().toString());
    assertEquals(1, Ilo.runCommands(new String[]{}).count());
  }

  @Test
  @DisplayName("does not need .rc files")
  void runsWithRunCommands(final SystemProperties properties) {
    properties.set("user.dir", testResources(Ilo.class).resolve("empty").toAbsolutePath().toString());
    assertEquals(0, Ilo.runCommands(new String[]{}).count());
  }

}
