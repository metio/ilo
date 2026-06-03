/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wtf.metio.ilo.os.OSSupport;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Override
  protected String currentUserCreateMapping() {
    // Computed the same way the production code does, so the expectation matches the running user.
    return "--user " + OSSupport.expander().expand("$(id -u):$(id -g)");
  }

  @Override
  protected boolean currentUserOnExec() {
    return true;
  }

  @Override
  protected boolean hintsAboutForeignFileOwnership() {
    return true;
  }

  @Test
  @DisplayName("suppresses the file-ownership hint on rootless Docker")
  void noHintOnRootlessDocker() {
    // Rootless Docker maps the container's root to the host user, so files stay owned by the caller.
    assertTrue(tool().currentUserHint(new ShellOptions(), args -> "[name=rootless]").isEmpty());
  }

  @Test
  @DisplayName("makes no file-ownership claim when no Docker daemon answers with security options")
  void noHintWithoutDockerSecurityOptions() {
    // The podman 'docker' shim has no SecurityOptions field and an unreachable daemon returns nothing;
    // neither is a rootful Docker daemon, so no hint is shown.
    assertTrue(tool().currentUserHint(new ShellOptions(), args -> "Error: can't evaluate field SecurityOptions").isEmpty());
  }

}
