/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RemoteUser")
class RemoteUserTest {

  // Distinguishes the inspect probe (returns the image's user) from the 'docker info' probe (reports
  // a rootful daemon); anything else returns nothing.
  private static final Function<List<String>, String> CAPTURE = args -> {
    if (args.contains("inspect")) {
      return "vscode";
    }
    if (args.contains("info")) {
      return "[name=seccomp,profile=builtin]";
    }
    return "";
  };

  @Test
  @DisplayName("maps nothing when the feature is disabled")
  void disabled() {
    final var options = options(false, "node", "image:test");
    RemoteUser.resolve(new Podman(), options, CAPTURE);
    assertEquals(RemoteUserMapping.NONE, options.userMapping);
  }

  @Test
  @DisplayName("keeps a non-root user with a keep-id namespace on podman and leaves the image alone")
  void podmanKeepId() {
    final var options = options(true, "node", "image:test");
    RemoteUser.resolve(new Podman(), options, CAPTURE);
    assertEquals(RemoteUserMapping.KEEP_ID, options.userMapping);
    assertNull(options.containerfile, "no derived image is built for keep-id");
  }

  @Test
  @DisplayName("builds a derived image to remap a non-root user on rootful Docker")
  void rootfulDockerRemap() {
    final var options = options(true, "node", "python:3");
    RemoteUser.resolve(new Docker(), options, CAPTURE);
    assertEquals(RemoteUserMapping.REMAP, options.userMapping);
    assertTrue(options.image.startsWith("ilo-remote-user:"), options.image);
    assertTrue(options.containerfile != null, "a derived containerfile is generated");
  }

  @Test
  @DisplayName("detects the image's user when --remote-user is omitted")
  void autoDetectsUser() {
    final var options = options(true, null, "image:test");
    RemoteUser.resolve(new Podman(), options, CAPTURE);
    assertEquals("vscode", options.remoteUser);
    assertEquals(RemoteUserMapping.KEEP_ID, options.userMapping);
  }

  private static ShellOptions options(final boolean enabled, final String remoteUser, final String image) {
    final var options = new ShellOptions();
    options.updateRemoteUserUID = enabled;
    options.remoteUser = remoteUser;
    options.image = image;
    return options;
  }

}
