/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.os;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("OS")
class OSTest {

  @Test
  @DisplayName("write passwd file")
  void passwd() throws Exception {
    SystemLambda.restoreSystemProperties(() -> {
      System.setProperty("user.name", "testuser");
      final var passwdFile = OS.passwdFile("1234:5678");
      final var content = Files.readString(passwdFile);
      assertEquals(content, "testuser:x:1234:5678::/home/testuser:/bin/bash");
    });
  }

}
