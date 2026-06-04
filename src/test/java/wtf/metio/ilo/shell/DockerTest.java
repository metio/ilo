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

@DisplayName("Docker")
class DockerTest extends DockerLikeTCK {

  @Override
  public ShellCLI tool() {
    return new Docker();
  }

  @Override
  protected ShellOptions options() {
    return new ShellOptions();
  }

  @Override
  protected String name() {
    return "docker";
  }

  @Override
  protected List<String> staleStatuses() {
    return List.of("created", "exited", "paused", "dead");
  }

  // On a rootful daemon Docker has no user namespace, so a non-root user is remapped via a derived
  // image and a root user is replaced with the bare host UID/GID.
  @Override
  protected RemoteUserMapping expectedNonRootMapping() {
    return RemoteUserMapping.REMAP;
  }

  @Override
  protected RemoteUserMapping expectedRootMapping() {
    return RemoteUserMapping.HOST_USER;
  }

  @Override
  protected Function<List<String>, String> rootfulCapture() {
    // A security-options listing without 'rootless' stands in for a rootful daemon.
    return args -> "[name=seccomp,profile=builtin]";
  }

  @Test
  @DisplayName("does not remap on rootless Docker, which maps the container root to the host user")
  void noMappingOnRootlessDocker() {
    assertEquals(RemoteUserMapping.NONE, tool().remoteUserMapping(true, "node", args -> "[name=rootless]"));
  }

  @Test
  @DisplayName("does not remap when no Docker daemon answers with security options")
  void noMappingWithoutDockerSecurityOptions() {
    // The podman 'docker' shim has no SecurityOptions field and an unreachable daemon returns nothing;
    // neither is treated as a rootful Docker daemon.
    assertEquals(RemoteUserMapping.NONE,
        tool().remoteUserMapping(true, "node", args -> "Error: can't evaluate field SecurityOptions"));
  }

}
