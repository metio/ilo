/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;
import wtf.metio.ilo.test.CliToolTCK;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SystemStubsExtension.class)
abstract class DockerLikeTCK extends CliToolTCK<ShellOptions, ShellCLI> {

  // A fixed container name stands in for the deterministic name the command derives at runtime.
  private static final String CONTAINER = "ilo-shell-0123456789ab";

  private ShellOptions minimal() {
    final var options = new ShellOptions();
    options.missingVolumes = ShellVolumeBehavior.CREATE;
    options.image = "example:test";
    return options;
  }

  @Test
  @DisplayName("generate pull arguments")
  void pullArguments() {
    final var options = minimal();
    options.pull = true;
    final var arguments = tool().pullArguments(options);
    assertEquals(String.format("%s pull example:test", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("generate build arguments")
  void buildArguments() {
    final var options = minimal();
    options.containerfile = "Dockerfile";
    final var arguments = tool().buildArguments(options);
    assertEquals(String.format("%s build --file Dockerfile --tag example:test", name()), String.join(" ", arguments));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("expands the containerfile path like the image and context")
  void expandsContainerfile(final SystemProperties properties) {
    properties.set("user.home", "/home/user");
    final var options = minimal();
    options.containerfile = "~/project/Containerfile";
    final var arguments = tool().buildArguments(options);
    assertTrue(String.join(" ", arguments).contains("--file /home/user/project/Containerfile"),
        () -> String.join(" ", arguments));
  }

  @Test
  @DisplayName("generate build arguments with pull")
  void buildArgumentsWithPull() {
    final var options = minimal();
    options.pull = true;
    options.containerfile = "Dockerfile";
    final var arguments = tool().buildArguments(options);
    assertEquals(String.format("%s build --file Dockerfile --pull --tag example:test", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("custom build context")
  void context() {
    final var options = minimal();
    options.context = ".";
    options.containerfile = "Dockerfile";
    final var arguments = tool().buildArguments(options);
    assertEquals(String.format("%s build --file Dockerfile --tag example:test .", name()), String.join(" ", arguments));
  }

  @Test
  @DisplayName("probes the container state by exact name")
  void probeArguments() {
    final var arguments = tool().probeArguments(minimal(), CONTAINER);
    assertEquals(
        String.format("%s ps --all --filter name=^%s$ --format {{.State}}", name(), CONTAINER),
        String.join(" ", arguments));
  }

  @Test
  @DisplayName("removes the container by name")
  void removeArguments() {
    final var arguments = tool().removeArguments(minimal(), CONTAINER);
    assertEquals(String.format("%s rm --force %s", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("creates a detached, named, labelled container that stays alive")
  void createArguments() {
    final var options = minimal();
    options.workingDir = "some/dir";
    final var arguments = tool().createArguments(options, CONTAINER);
    assertEquals(
        String.format("%s run --detach --name %s --label ilo.managed=true --label ilo.project=%s --workdir some/dir --env ILO_CONTAINER=true --entrypoint sh example:test -c trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done", name(), CONTAINER, System.getProperty("user.dir")),
        String.join(" ", arguments));
  }

  @Test
  @DisplayName("leaves the image's entrypoint and command untouched when the override is off")
  void createWithoutOverrideCommand() {
    final var options = minimal();
    options.workingDir = "some/dir";
    options.overrideCommand = false;
    final var arguments = tool().createArguments(options, CONTAINER);
    // No --entrypoint and no keepalive command: the image's own long-running process keeps it alive.
    assertEquals(
        String.format("%s run --detach --name %s --label ilo.managed=true --label ilo.project=%s --workdir some/dir --env ILO_CONTAINER=true example:test", name(), CONTAINER, System.getProperty("user.dir")),
        String.join(" ", arguments));
  }

  @Test
  @DisplayName("mounts the project directory into the created container")
  void createMountsProjectDir(final SystemProperties properties) {
    properties.set("user.dir", "/some/folder");
    final var options = minimal();
    options.mountProjectDir = true;
    options.workingDir = "";
    final var arguments = tool().createArguments(options, CONTAINER);
    assertEquals(
        String.format("%s run --detach --name %s --label ilo.managed=true --label ilo.project=/some/folder --volume /some/folder:/some/folder:z --workdir /some/folder --env ILO_CONTAINER=true --entrypoint sh example:test -c trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done", name(), CONTAINER),
        String.join(" ", arguments));
  }

  @Test
  @DisplayName("passes runtime run options to the created container")
  void createWithRuntimeRunOption(final SystemProperties properties) {
    properties.set("user.dir", "/work");
    final var options = minimal();
    options.workingDir = "some/dir";
    options.runtimeRunOptions = List.of("--quiet");
    final var arguments = tool().createArguments(options, CONTAINER);
    assertEquals(
        String.format("%s run --detach --name %s --label ilo.managed=true --label ilo.project=/work --quiet --workdir some/dir --env ILO_CONTAINER=true --entrypoint sh example:test -c trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done", name(), CONTAINER),
        String.join(" ", arguments));
  }

  @Test
  @DisplayName("sets the hostname on the created container")
  void createWithHostname(final SystemProperties properties) {
    properties.set("user.dir", "/work");
    final var options = minimal();
    options.hostname = "some-test";
    options.workingDir = "some/dir";
    final var arguments = tool().createArguments(options, CONTAINER);
    assertEquals(
        String.format("%s run --detach --name %s --label ilo.managed=true --label ilo.project=/work --workdir some/dir --env ILO_CONTAINER=true --hostname some-test --entrypoint sh example:test -c trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done", name(), CONTAINER),
        String.join(" ", arguments));
  }

  @Test
  @DisplayName("adds environment variables to the created container")
  void createWithVariables(final SystemProperties properties) {
    properties.set("user.dir", "/work");
    final var options = minimal();
    options.variables = List.of("KEY=value", "OTHER=value");
    options.workingDir = "some/dir";
    final var arguments = tool().createArguments(options, CONTAINER);
    assertEquals(
        String.format("%s run --detach --name %s --label ilo.managed=true --label ilo.project=/work --workdir some/dir --env ILO_CONTAINER=true --env KEY=value --env OTHER=value --entrypoint sh example:test -c trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done", name(), CONTAINER),
        String.join(" ", arguments));
  }

  @Test
  @DisplayName("uses an explicit workspace mount instead of the default project volume")
  void createWithWorkspaceMount(final SystemProperties properties) {
    properties.set("user.dir", "/work");
    final var options = minimal();
    options.mountProjectDir = true;
    options.workspaceMount = "type=bind,source=/host,target=/w";
    options.workingDir = "/w";
    final var arguments = tool().createArguments(options, CONTAINER);
    assertEquals(
        String.format("%s run --detach --name %s --label ilo.managed=true --label ilo.project=/work --mount type=bind,source=/host,target=/w --workdir /w --env ILO_CONTAINER=true --entrypoint sh example:test -c trap 'exit 0' TERM INT; while true; do sleep 2147483647 & wait $!; done", name(), CONTAINER),
        String.join(" ", arguments));
  }

  @Test
  @DisplayName("starts the existing container by name")
  void startArguments() {
    final var arguments = tool().startArguments(minimal(), CONTAINER);
    assertEquals(String.format("%s start %s", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("attaches with the default shell when no command is given")
  void attachDefaultShell() {
    final var options = minimal();
    options.shell = "/bin/sh";
    final var arguments = tool().attachArguments(options, CONTAINER);
    assertEquals(String.format("%s exec %s /bin/sh", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("attaches interactively when requested")
  void attachInteractive() {
    final var options = minimal();
    options.shell = "/bin/sh";
    options.interactive = true;
    final var arguments = tool().attachArguments(options, CONTAINER);
    // --tty is only added when attached to a real terminal, which the test JVM is not.
    assertEquals(String.format("%s exec --interactive %s /bin/sh", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("attaches with a custom command instead of the shell")
  void attachCustomCommand() {
    final var options = minimal();
    options.commands = List.of("/bin/bash", "-c", "whoami");
    final var arguments = tool().attachArguments(options, CONTAINER);
    assertEquals(String.format("%s exec %s /bin/bash -c whoami", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("appends shell arguments to the interactive shell")
  void attachWithShellArguments() {
    final var options = minimal();
    options.shell = "/bin/bash";
    options.shellArguments = List.of("-l", "-i");
    final var arguments = tool().attachArguments(options, CONTAINER);
    assertEquals(String.format("%s exec %s /bin/bash -l -i", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("ignores shell arguments when an explicit command is given")
  void attachShellArgumentsIgnoredWithCommand() {
    final var options = minimal();
    options.commands = List.of("ls");
    options.shellArguments = List.of("-l");
    final var arguments = tool().attachArguments(options, CONTAINER);
    assertEquals(String.format("%s exec %s ls", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("applies remoteEnv on the attach exec rather than the container")
  void attachWithRemoteEnv() {
    final var options = minimal();
    options.shell = "/bin/sh";
    options.remoteVariables = List.of("FOO=bar");
    final var arguments = tool().attachArguments(options, CONTAINER);
    assertEquals(String.format("%s exec --env FOO=bar %s /bin/sh", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("execs a lifecycle command into the running container")
  void execArguments() {
    final var arguments = tool().execArguments(minimal(), CONTAINER, List.of("sh", "-c", "echo hi"));
    assertEquals(String.format("%s exec %s sh -c echo hi", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("applies remoteEnv on a lifecycle exec")
  void execWithRemoteEnv() {
    final var options = minimal();
    options.remoteVariables = List.of("FOO=bar");
    final var arguments = tool().execArguments(options, CONTAINER, List.of("sh", "-c", "echo hi"));
    assertEquals(String.format("%s exec --env FOO=bar %s sh -c echo hi", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("lists this project's stopped containers for cleanup")
  void staleContainersArguments() {
    final var arguments = tool().staleContainersArguments(minimal(), "/work");
    final var statusFilters = String.join(" ",
        staleStatuses().stream().map(status -> "--filter status=" + status).toList());
    assertEquals(
        String.format("%s ps --all --filter label=ilo.project=/work %s --format {{.Names}}", name(), statusFilters),
        String.join(" ", arguments));
  }

  // The non-running states this runtime sweeps; Docker additionally has 'dead'.
  protected List<String> staleStatuses() {
    return List.of("created", "exited", "paused");
  }

  @Test
  @DisplayName("lists the container's processes to detect attached sessions")
  void processesArguments() {
    final var arguments = tool().processesArguments(minimal(), CONTAINER);
    assertEquals(String.format("%s top %s", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("inspects the container's main process host PID")
  void mainPidArguments() {
    final var arguments = tool().mainPidArguments(minimal(), CONTAINER);
    assertEquals(String.format("%s inspect --format {{.State.Pid}} %s", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("applies the resolved user mapping on create")
  void createAppliesUserMapping() {
    final var options = minimal();
    options.userMapping = RemoteUserMapping.KEEP_ID;
    assertTrue(String.join(" ", tool().createArguments(options, CONTAINER)).contains("--userns=keep-id"));
  }

  @Test
  @DisplayName("requests the user on exec for a mapping that does not survive in a namespace")
  void attachRequestsUserForHostMapping() {
    final var options = minimal();
    options.userMapping = RemoteUserMapping.HOST_USER;
    assertTrue(String.join(" ", tool().attachArguments(options, CONTAINER)).contains("--user"));
  }

  @Test
  @DisplayName("inherits the user namespace on exec for a keep-id mapping")
  void attachInheritsKeepId() {
    final var options = minimal();
    options.userMapping = RemoteUserMapping.KEEP_ID;
    assertFalse(String.join(" ", tool().attachArguments(options, CONTAINER)).contains("--user"));
  }

  @Test
  @DisplayName("resolves a mapping for a non-root user")
  void resolvesNonRootUser() {
    assertEquals(expectedNonRootMapping(), tool().remoteUserMapping(true, "node", rootfulCapture()));
  }

  @Test
  @DisplayName("resolves a mapping for the root user")
  void resolvesRootUser() {
    assertEquals(expectedRootMapping(), tool().remoteUserMapping(true, "root", rootfulCapture()));
  }

  @Test
  @DisplayName("resolves no mapping when the feature is disabled")
  void resolvesDisabled() {
    assertEquals(RemoteUserMapping.NONE, tool().remoteUserMapping(false, "node", rootfulCapture()));
  }

  @Test
  @DisplayName("reports whether it supports a UID-pinned keep-id namespace")
  void keepIdUidSupport() {
    assertEquals(expectedKeepIdUidSupport(), tool().supportsKeepIdUid());
  }

  // Only podman supports '--userns=keep-id:uid=…,gid=…'.
  protected boolean expectedKeepIdUidSupport() {
    return false;
  }

  // Podman/nerdctl map a non-root user with a keep-id namespace and need nothing for root. Docker
  // overrides these to remap on a rootful daemon and to request the host user when there is none.
  protected RemoteUserMapping expectedNonRootMapping() {
    return RemoteUserMapping.KEEP_ID;
  }

  protected RemoteUserMapping expectedRootMapping() {
    return RemoteUserMapping.NONE;
  }

  // The capture used while resolving: podman/nerdctl ignore it; Docker reads 'docker info' from it.
  protected Function<List<String>, String> rootfulCapture() {
    return args -> "";
  }

  @Test
  @DisplayName("stops the container by name")
  void stopArguments() {
    final var arguments = tool().stopArguments(minimal(), CONTAINER);
    assertEquals(String.format("%s stop %s", name(), CONTAINER), String.join(" ", arguments));
  }

  @Test
  @DisplayName("generate clean arguments")
  void cleanArguments() {
    final var options = minimal();
    options.removeImage = true;
    final var arguments = tool().cleanupArguments(options);
    assertEquals(String.format("%s rmi example:test", name()), String.join(" ", arguments));
  }

}
