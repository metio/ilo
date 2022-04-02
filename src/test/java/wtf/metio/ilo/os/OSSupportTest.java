/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.os;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("OS")
@ExtendWith(SystemStubsExtension.class)
class OSSupportTest {

  @Test
  @DisplayName("write passwd file")
  void passwd(final SystemProperties properties) throws Exception {
    properties.set("user.name", "testuser");
    final var passwdFile = OSSupport.passwdFile("1234:5678");
    assertNotNull(passwdFile);
    final var content = Files.readString(passwdFile);
    assertEquals(content, "testuser:x:1234:5678::/home/testuser:/bin/bash");
  }

}
