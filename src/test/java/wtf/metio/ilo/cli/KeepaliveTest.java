/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Keepalive")
class KeepaliveTest {

  @Test
  @DisplayName("renders the entrypoint and script as a single 'sh -c <script>' argument list")
  void command() {
    assertEquals(List.of("sh", "-c", Keepalive.SCRIPT), Keepalive.command());
  }

}
