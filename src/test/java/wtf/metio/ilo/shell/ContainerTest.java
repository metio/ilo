/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;
import wtf.metio.ilo.cli.ContainerState;
import wtf.metio.ilo.test.CliToolTCK;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Container")
@ExtendWith(SystemStubsExtension.class)
class ContainerTest extends CliToolTCK<ShellOptions, ShellCLI> {

  // A fixed container name stands in for the deterministic name the command derives at runtime.
  private static final String NAME = "ilo-shell-0123456789ab";

  @Override
  protected ShellCLI tool() {
    return new Container();
  }

  @Override
  protected ShellOptions options() {
    return minimal();
  }

  @Override
  protected String name() {
    return "container";
  }

  private ShellOptions minimal() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.image = "example:test";
    return options;
  }

  @Test
  @DisplayName("pulls through the namespaced image command, not a top-level pull")
  void pullImage() {
    final var options = minimal();
    options.pull = true;
    assertEquals("container image pull example:test", String.join(" ", tool().pullArguments(options)));
  }

  @Test
  @DisplayName("does not pull when a containerfile is built")
  void pullSkippedWithContainerfile() {
    final var options = minimal();
    options.pull = true;
    options.containerfile = "Dockerfile";
    assertTrue(tool().pullArguments(options).isEmpty());
  }

  @Test
  @DisplayName("builds from a containerfile")
  void build() {
    final var options = minimal();
    options.containerfile = "Dockerfile";
    assertEquals("container build --file Dockerfile --tag example:test", String.join(" ", tool().buildArguments(options)));
  }

  @Test
  @DisplayName("builds with a refreshed base image")
  void buildWithPull() {
    final var options = minimal();
    options.pull = true;
    options.containerfile = "Dockerfile";
    assertEquals("container build --file Dockerfile --pull --tag example:test", String.join(" ", tool().buildArguments(options)));
  }

  @Test
  @DisplayName("probes by listing every container as JSON (no name filter is available)")
  void probeList() {
    assertEquals("container list --all --format json", String.join(" ", tool().probeArguments(minimal(), NAME)));
  }

  @Test
  @DisplayName("reads the probed container's state from the JSON listing")
  void probeStateRunning() {
    final Function<List<String>, String> capture = _ ->
        "[{\"status\":\"running\",\"configuration\":{\"id\":\"" + NAME + "\"}}]";
    assertEquals(ContainerState.RUNNING, tool().probeState(minimal(), NAME, capture));
  }

  @Test
  @DisplayName("reports an absent container when the listing has no matching entry")
  void probeStateAbsent() {
    assertEquals(ContainerState.ABSENT, tool().probeState(minimal(), NAME, _ -> "[]"));
  }

  @Test
  @DisplayName("removes the container forcibly")
  void remove() {
    assertEquals("container delete --force " + NAME, String.join(" ", tool().removeArguments(minimal(), NAME)));
  }

  @Test
  @DisplayName("creates a detached, named, labelled container that stays alive")
  void create() {
    final var options = minimal();
    options.workingDir = "some/dir";
    assertEquals(
        String.format("container run --detach --name %s --label ilo.managed=true --label ilo.project=%s --workdir some/dir --env ILO_CONTAINER=true --entrypoint sh example:test -c trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done", NAME, System.getProperty("user.dir")),
        String.join(" ", tool().createArguments(options, NAME)));
  }

  @Test
  @DisplayName("leaves the image's entrypoint and command untouched when the override is off")
  void createWithoutOverrideCommand() {
    final var options = minimal();
    options.workingDir = "some/dir";
    options.overrideCommand = false;
    assertEquals(
        String.format("container run --detach --name %s --label ilo.managed=true --label ilo.project=%s --workdir some/dir --env ILO_CONTAINER=true example:test", NAME, System.getProperty("user.dir")),
        String.join(" ", tool().createArguments(options, NAME)));
  }

  @Test
  @DisplayName("mounts the project directory with a plain --volume, without the SELinux relabel")
  void createMountsProjectDir(final SystemProperties properties) {
    properties.set("user.dir", "/some/folder");
    final var options = minimal();
    options.mountProjectDir = true;
    options.workingDir = "";
    assertEquals(
        String.format("container run --detach --name %s --label ilo.managed=true --label ilo.project=/some/folder --volume /some/folder:/some/folder --workdir /some/folder --env ILO_CONTAINER=true --entrypoint sh example:test -c trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done", NAME),
        String.join(" ", tool().createArguments(options, NAME)));
  }

  @Test
  @DisplayName("uses an explicit workspace mount instead of the default project volume")
  void createWithWorkspaceMount(final SystemProperties properties) {
    properties.set("user.dir", "/work");
    final var options = minimal();
    options.mountProjectDir = true;
    options.workspaceMount = "type=bind,source=/host,target=/w";
    options.workingDir = "/w";
    assertEquals(
        String.format("container run --detach --name %s --label ilo.managed=true --label ilo.project=/work --mount type=bind,source=/host,target=/w --workdir /w --env ILO_CONTAINER=true --entrypoint sh example:test -c trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done", NAME),
        String.join(" ", tool().createArguments(options, NAME)));
  }

  @Test
  @DisplayName("sets env vars and ports on the created container")
  void createWithExtras(final SystemProperties properties) {
    properties.set("user.dir", "/work");
    final var options = minimal();
    options.workingDir = "some/dir";
    options.variables = List.of("KEY=value");
    options.ports = List.of("8080:80");
    assertEquals(
        String.format("container run --detach --name %s --label ilo.managed=true --label ilo.project=/work --workdir some/dir --env ILO_CONTAINER=true --env KEY=value --publish 8080:80 --entrypoint sh example:test -c trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done", NAME),
        String.join(" ", tool().createArguments(options, NAME)));
  }

  @Test
  @DisplayName("does not support --hostname")
  void doesNotSupportHostname() {
    assertFalse(tool().supportsHostname());
  }

  @Test
  @DisplayName("never maps users through a user namespace, since 'run' has no --userns")
  void remoteUserMappingAvoidsUserns() {
    // The default mapping would pick KEEP_ID (--userns=keep-id) for a non-root user; this runtime maps
    // ownership in its VM instead, like Docker Desktop, so it stays NONE.
    assertEquals(RemoteUserMapping.NONE, tool().remoteUserMapping(true, "node", _ -> ""));
    assertEquals(RemoteUserMapping.NONE, tool().remoteUserMapping(true, "root", _ -> ""));
    assertEquals(RemoteUserMapping.NONE, tool().remoteUserMapping(false, "node", _ -> ""));
  }

  @Test
  @DisplayName("does not support a UID-pinned keep-id namespace")
  void doesNotSupportKeepIdUid() {
    assertFalse(tool().supportsKeepIdUid());
  }

  @Test
  @DisplayName("drops a configured hostname instead of emitting the unsupported flag")
  void createDropsHostname(final SystemProperties properties) {
    properties.set("user.dir", "/work");
    final var options = minimal();
    options.workingDir = "some/dir";
    options.hostname = "host";
    assertFalse(String.join(" ", tool().createArguments(options, NAME)).contains("--hostname"));
  }

  @Test
  @DisplayName("starts the existing container by name")
  void start() {
    assertEquals("container start " + NAME, String.join(" ", tool().startArguments(minimal(), NAME)));
  }

  @Test
  @DisplayName("attaches with the default shell when no command is given")
  void attachDefaultShell() {
    final var options = minimal();
    options.shell = "/bin/sh";
    assertEquals("container exec " + NAME + " /bin/sh", String.join(" ", tool().attachArguments(options, NAME)));
  }

  @Test
  @DisplayName("attaches interactively when requested")
  void attachInteractive() {
    final var options = minimal();
    options.shell = "/bin/sh";
    options.interactive = true;
    // --tty is only added when attached to a real terminal, which the test JVM is not.
    assertEquals("container exec --interactive " + NAME + " /bin/sh", String.join(" ", tool().attachArguments(options, NAME)));
  }

  @Test
  @DisplayName("applies remoteEnv on the attach exec")
  void attachWithRemoteEnv() {
    final var options = minimal();
    options.shell = "/bin/sh";
    options.remoteVariables = List.of("FOO=bar");
    assertEquals("container exec --env FOO=bar " + NAME + " /bin/sh", String.join(" ", tool().attachArguments(options, NAME)));
  }

  @Test
  @DisplayName("execs a lifecycle command into the running container")
  void exec() {
    assertEquals("container exec " + NAME + " sh -c echo hi",
        String.join(" ", tool().execArguments(minimal(), NAME, List.of("sh", "-c", "echo hi"))));
  }

  @Test
  @DisplayName("stops the container by name")
  void stop() {
    assertEquals("container stop " + NAME, String.join(" ", tool().stopArguments(minimal(), NAME)));
  }

  @Test
  @DisplayName("removes the image through the namespaced image command, not a top-level rmi")
  void cleanup() {
    final var options = minimal();
    options.removeImage = true;
    assertEquals("container image delete example:test", String.join(" ", tool().cleanupArguments(options)));
  }

  @Test
  @DisplayName("does not clean up the image unless requested")
  void cleanupSkipped() {
    assertTrue(tool().cleanupArguments(minimal()).isEmpty());
  }

  @Test
  @DisplayName("lists every container as JSON for the stale sweep (no label filter is available)")
  void staleList() {
    assertEquals("container list --all --format json", String.join(" ", tool().staleContainersArguments(minimal(), "/work")));
  }

  @Test
  @DisplayName("selects this project's non-running containers from the JSON listing")
  void staleContainers() {
    final Function<List<String>, String> capture = _ -> "["
        + "{\"status\":\"exited\",\"configuration\":{\"id\":\"ilo-old\",\"labels\":{\"ilo.project\":\"/work\"}}},"
        + "{\"status\":\"running\",\"configuration\":{\"id\":\"ilo-live\",\"labels\":{\"ilo.project\":\"/work\"}}},"
        + "{\"status\":\"exited\",\"configuration\":{\"id\":\"ilo-elsewhere\",\"labels\":{\"ilo.project\":\"/other\"}}}"
        + "]";
    assertEquals(List.of("ilo-old"), tool().staleContainers(minimal(), "/work", capture));
  }

  @Test
  @DisplayName("does not introspect sessions: there is no host-side process listing")
  void processesUnsupported() {
    assertTrue(tool().processesArguments(minimal(), NAME).isEmpty());
  }

  @Test
  @DisplayName("does not introspect a main PID: inspect exposes none")
  void mainPidUnsupported() {
    assertTrue(tool().mainPidArguments(minimal(), NAME).isEmpty());
  }

}
