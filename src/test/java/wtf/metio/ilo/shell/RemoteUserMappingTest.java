/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.os.OSSupport;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DisplayName("RemoteUserMapping")
class RemoteUserMappingTest {

  private static final boolean DOCKER = true;
  private static final boolean PODMAN = false;
  private static final boolean ROOTFUL = true;
  private static final boolean ENABLED = true;

  @Nested
  @DisplayName("resolve")
  class Resolve {

    @Test
    @DisplayName("does nothing when the feature is disabled")
    void disabled() {
      assertEquals(RemoteUserMapping.NONE, RemoteUserMapping.resolve(DOCKER, ROOTFUL, "node", false));
    }

    @Test
    @DisplayName("remaps a non-root user on rootful Docker")
    void rootfulDockerNonRoot() {
      assertEquals(RemoteUserMapping.REMAP, RemoteUserMapping.resolve(DOCKER, ROOTFUL, "node", ENABLED));
    }

    @Test
    @DisplayName("runs as the host user on rootful Docker when there is no non-root user to remap")
    void rootfulDockerRoot() {
      assertEquals(RemoteUserMapping.HOST_USER, RemoteUserMapping.resolve(DOCKER, ROOTFUL, "root", ENABLED));
    }

    @Test
    @DisplayName("treats a missing user as root on rootful Docker")
    void rootfulDockerMissingUser() {
      assertEquals(RemoteUserMapping.HOST_USER, RemoteUserMapping.resolve(DOCKER, ROOTFUL, null, ENABLED));
    }

    @Test
    @DisplayName("does nothing on rootless Docker, which maps the container root to the host user")
    void rootlessDocker() {
      assertEquals(RemoteUserMapping.NONE, RemoteUserMapping.resolve(DOCKER, false, "node", ENABLED));
    }

    @Test
    @DisplayName("uses a keep-id user namespace for a non-root user on podman/nerdctl")
    void podmanNonRoot() {
      assertEquals(RemoteUserMapping.KEEP_ID, RemoteUserMapping.resolve(PODMAN, false, "node", ENABLED));
    }

    @Test
    @DisplayName("does nothing for a root user on a rootless runtime, which already maps root to the host user")
    void podmanRoot() {
      assertEquals(RemoteUserMapping.NONE, RemoteUserMapping.resolve(PODMAN, false, "root", ENABLED));
    }

    @Test
    @DisplayName("treats the numeric root uid as root")
    void numericRoot() {
      assertEquals(RemoteUserMapping.NONE, RemoteUserMapping.resolve(PODMAN, false, "0", ENABLED));
    }

    @Test
    @DisplayName("treats a blank user as root")
    void blankUser() {
      assertEquals(RemoteUserMapping.NONE, RemoteUserMapping.resolve(PODMAN, false, "  ", ENABLED));
    }
  }

  @Nested
  @DisplayName("createArguments")
  class CreateArguments {

    private static final OSSupport.Expander EXPAND = OSSupport.expander();

    @Test
    @DisplayName("NONE runs as a named user without aligning it")
    void noneNamed() {
      assertIterableEquals(List.of("--user", "node"), RemoteUserMapping.NONE.createArguments("node", null, null, EXPAND));
    }

    @Test
    @DisplayName("NONE contributes nothing for the root user")
    void noneRoot() {
      assertIterableEquals(List.of(), RemoteUserMapping.NONE.createArguments("root", null, null, EXPAND));
    }

    @Test
    @DisplayName("KEEP_ID pins the keep-id namespace to the user's UID/GID when known")
    void keepIdWithIds() {
      assertIterableEquals(List.of("--userns=keep-id:uid=1000,gid=1000", "--user", "node"),
          RemoteUserMapping.KEEP_ID.createArguments("node", "1000", "1000", EXPAND));
    }

    @Test
    @DisplayName("KEEP_ID falls back to a plain keep-id namespace when the UID/GID are unknown")
    void keepIdWithoutIds() {
      assertIterableEquals(List.of("--userns=keep-id", "--user", "node"),
          RemoteUserMapping.KEEP_ID.createArguments("node", null, null, EXPAND));
    }

    @Test
    @DisplayName("KEEP_ID falls back to a plain keep-id namespace when only one id is known")
    void keepIdWithPartialIds() {
      assertIterableEquals(List.of("--userns=keep-id", "--user", "node"),
          RemoteUserMapping.KEEP_ID.createArguments("node", "1000", null, EXPAND));
    }

    @Test
    @DisplayName("HOST_USER requests the host UID and GID")
    void hostUser() {
      assertIterableEquals(List.of("--user", EXPAND.expand("$(id -u):$(id -g)")),
          RemoteUserMapping.HOST_USER.createArguments("root", null, null, EXPAND));
    }

    @Test
    @DisplayName("REMAP runs as the remapped user by name")
    void remap() {
      assertIterableEquals(List.of("--user", "node"), RemoteUserMapping.REMAP.createArguments("node", null, null, EXPAND));
    }
  }

  @Nested
  @DisplayName("execArguments")
  class ExecArguments {

    private static final OSSupport.Expander EXPAND = OSSupport.expander();

    @Test
    @DisplayName("NONE repeats a named user on exec so a Docker exec does not default to root")
    void noneNamed() {
      assertIterableEquals(List.of("--user", "node"), RemoteUserMapping.NONE.execArguments("node", EXPAND));
    }

    @Test
    @DisplayName("NONE contributes nothing on exec for the root user")
    void noneRoot() {
      assertIterableEquals(List.of(), RemoteUserMapping.NONE.execArguments("root", EXPAND));
    }

    @Test
    @DisplayName("KEEP_ID needs nothing on exec, which inherits the container's user namespace")
    void keepId() {
      assertIterableEquals(List.of(), RemoteUserMapping.KEEP_ID.execArguments("node", EXPAND));
    }

    @Test
    @DisplayName("HOST_USER repeats the host UID and GID on exec")
    void hostUser() {
      assertIterableEquals(List.of("--user", EXPAND.expand("$(id -u):$(id -g)")),
          RemoteUserMapping.HOST_USER.execArguments("node", EXPAND));
    }

    @Test
    @DisplayName("REMAP repeats the remapped user on exec")
    void remap() {
      assertIterableEquals(List.of("--user", "node"), RemoteUserMapping.REMAP.execArguments("node", EXPAND));
    }
  }

}
