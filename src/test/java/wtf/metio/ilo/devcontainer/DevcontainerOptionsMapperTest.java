/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.devcontainer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import wtf.metio.devcontainer.BuildBuilder;
import wtf.metio.devcontainer.DevcontainerBuilder;
import wtf.metio.devcontainer.Mount;
import wtf.metio.devcontainer.MountObject;
import wtf.metio.devcontainer.MountType;
import wtf.metio.devcontainer.ShutdownAction;
import wtf.metio.devcontainer.UserEnvProbe;
import wtf.metio.ilo.shell.ShellVolumeBehavior;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static wtf.metio.ilo.devcontainer.DevcontainerOptionsMapper.composeOptions;
import static wtf.metio.ilo.devcontainer.DevcontainerOptionsMapper.shellOptions;

@DisplayName("DevcontainerOptionsMapper")
@ExtendWith(SystemStubsExtension.class)
class DevcontainerOptionsMapperTest {

  @Nested
  @DisplayName("shell options")
  class ShellOptionsMapper {

    @Test
    @DisplayName("returns non-null values")
    void shouldReturnNonNullValues() {
      assertNotNull(shellOptions(new DevcontainerOptions(), DevcontainerBuilder.builder().create()));
    }

    @Test
    @DisplayName("maps the image field")
    void shouldMapImage() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().image("example:123").create();

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertEquals(json.image(), shellOptions.image);
    }

    @Test
    @DisplayName("maps the dockerFile field")
    void shouldMapDockerfile() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().build(BuildBuilder.builder().dockerfile("some.dockerfile").create()).create();

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertEquals(json.build().dockerfile(), shellOptions.containerfile);
    }

    @Test
    @DisplayName("maps the context field")
    void shouldMapContext() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().build(BuildBuilder.builder().context("example").create()).create();

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertEquals(json.build().context(), shellOptions.context);
    }

    @Test
    @DisplayName("maps the forwardPorts field")
    void shouldMapForwardPorts() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().forwardPorts(List.of("123", "456")).create();

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertIterableEquals(List.of("123:123", "456:456"), shellOptions.ports);
    }

    @Test
    @DisplayName("maps the runArgs field")
    void shouldMapRunArgs() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().runArgs(List.of("--cap-add=SYS_PTRACE", "--security-opt")).create();

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertIterableEquals(List.of("--cap-add=SYS_PTRACE", "--security-opt"), shellOptions.runtimeRunOptions);
    }

    @Test
    @DisplayName("--interactive is set to true")
    void shouldEnableInteractiveMode() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().create();

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertTrue(shellOptions.interactive);
    }

    @Test
    @DisplayName("--missing-volumes is set to CREATE")
    void shouldCreateMissingVolumes() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().create();

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertEquals(ShellVolumeBehavior.CREATE, shellOptions.missingVolumes);
    }

    @Test
    @DisplayName("sets the default context in case none is specified")
    void shouldUseDefaultForMissingContext() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().create();

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertEquals(".", shellOptions.context);
    }

    @Test
    @DisplayName("sets the default dockerfile in case none is specified")
    void shouldUseDefaultForMissingDockerfile() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().create();

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertEquals("", shellOptions.containerfile);
    }

    @Test
    @DisplayName("maps the shell option")
    void shouldMapShell() {
      final var options = new DevcontainerOptions();
      options.shell = "/bin/zsh";
      assertEquals("/bin/zsh", shellOptions(options, DevcontainerBuilder.builder().create()).shell);
    }

    @Test
    @DisplayName("maps the fresh flag")
    void shouldMapFresh() {
      final var options = new DevcontainerOptions();
      options.fresh = true;
      assertTrue(shellOptions(options, DevcontainerBuilder.builder().create()).fresh);
    }

    @Test
    @DisplayName("overrides the command by default when the definition is silent")
    void shouldOverrideCommandByDefault() {
      assertTrue(shellOptions(new DevcontainerOptions(), DevcontainerBuilder.builder().create()).overrideCommand);
    }

    @Test
    @DisplayName("honours overrideCommand=false from the devcontainer definition")
    void shouldRespectOverrideCommandFalse() {
      final var json = DevcontainerBuilder.builder().overrideCommand(false).create();
      assertFalse(shellOptions(new DevcontainerOptions(), json).overrideCommand);
    }

    @Test
    @DisplayName("maps workspaceFolder to the working directory")
    void shouldMapWorkspaceFolder() {
      final var json = DevcontainerBuilder.builder().workspaceFolder("/workspaces/project").create();
      assertEquals("/workspaces/project", shellOptions(new DevcontainerOptions(), json).workingDir);
    }

    @Test
    @DisplayName("maps containerEnv to --env values")
    void shouldMapContainerEnv() {
      final var json = DevcontainerBuilder.builder().containerEnv(Map.of("KEY", "value")).create();
      assertIterableEquals(List.of("KEY=value"), shellOptions(new DevcontainerOptions(), json).variables);
    }

    @Test
    @DisplayName("drops containerEnv entries with a null value instead of emitting NAME=null")
    void shouldDropNullContainerEnv() {
      final var env = new HashMap<String, String>();
      env.put("KEEP", "value");
      env.put("DROP", null);
      final var json = DevcontainerBuilder.builder().containerEnv(env).create();
      assertIterableEquals(List.of("KEEP=value"), shellOptions(new DevcontainerOptions(), json).variables);
    }

    @Test
    @DisplayName("maps remoteEnv to remote (exec) variables, dropping null values")
    void shouldMapRemoteEnv() {
      final var env = new HashMap<String, String>();
      env.put("KEEP", "value");
      env.put("DROP", null);
      final var json = DevcontainerBuilder.builder().remoteEnv(env).create();
      assertIterableEquals(List.of("KEEP=value"), shellOptions(new DevcontainerOptions(), json).remoteVariables);
    }

    @Test
    @DisplayName("maps userEnvProbe to interactive shell arguments")
    void shouldMapUserEnvProbe() {
      assertAll(
          () -> assertEquals(List.of("-l", "-i"), shellArgumentsFor(UserEnvProbe.loginInteractiveShell)),
          () -> assertEquals(List.of("-l"), shellArgumentsFor(UserEnvProbe.loginShell)),
          () -> assertEquals(List.of("-i"), shellArgumentsFor(UserEnvProbe.interactiveShell)),
          () -> assertEquals(List.of(), shellArgumentsFor(UserEnvProbe.none)));
    }

    @Test
    @DisplayName("adds no shell arguments when userEnvProbe is unset")
    void shouldNotMapAbsentUserEnvProbe() {
      assertEquals(List.of(),
          shellOptions(new DevcontainerOptions(), DevcontainerBuilder.builder().create()).shellArguments);
    }

    private List<String> shellArgumentsFor(final UserEnvProbe probe) {
      final var json = DevcontainerBuilder.builder().userEnvProbe(probe).create();
      return shellOptions(new DevcontainerOptions(), json).shellArguments;
    }

    @Test
    @DisplayName("maps the workspaceMount override")
    void shouldMapWorkspaceMount() {
      final var json = DevcontainerBuilder.builder().workspaceMount("type=bind,source=/h,target=/w").create();
      assertEquals("type=bind,source=/h,target=/w", shellOptions(new DevcontainerOptions(), json).workspaceMount);
    }

    @Test
    @DisplayName("keeps the container running on exit for shutdownAction none")
    void shouldKeepRunningForShutdownActionNone() {
      final var json = DevcontainerBuilder.builder().shutdownAction(ShutdownAction.none).create();
      assertTrue(shellOptions(new DevcontainerOptions(), json).keepRunningOnExit);
    }

    @Test
    @DisplayName("stops the container on exit when shutdownAction is unset")
    void shouldNotKeepRunningByDefault() {
      assertFalse(shellOptions(new DevcontainerOptions(), DevcontainerBuilder.builder().create()).keepRunningOnExit);
    }

    @Test
    @DisplayName("maps an object-form mount to --mount honoring its declared type")
    void shouldMapObjectMount() {
      final var json = DevcontainerBuilder.builder()
          .mounts(List.of(new Mount(null, new MountObject(MountType.bind, "/host/cache", "/cache"))))
          .create();
      assertIterableEquals(List.of("--mount", "type=bind,source=/host/cache,target=/cache"),
          shellOptions(new DevcontainerOptions(), json).runtimeRunOptions);
    }

    @Test
    @DisplayName("keeps a source-less anonymous volume mount")
    void shouldMapAnonymousVolumeMount() {
      final var json = DevcontainerBuilder.builder()
          .mounts(List.of(new Mount(null, new MountObject(MountType.volume, null, "/cache"))))
          .create();
      assertIterableEquals(List.of("--mount", "type=volume,target=/cache"),
          shellOptions(new DevcontainerOptions(), json).runtimeRunOptions);
    }

    @Test
    @DisplayName("forwards a string-form mount verbatim")
    void shouldForwardStringMount() {
      final var json = DevcontainerBuilder.builder()
          .mounts(List.of(new Mount("source=dind,target=/var/lib/docker,type=volume", null)))
          .create();
      assertIterableEquals(List.of("--mount", "source=dind,target=/var/lib/docker,type=volume"),
          shellOptions(new DevcontainerOptions(), json).runtimeRunOptions);
    }

    @Test
    @DisplayName("ignores a mount that carries neither a string nor a populated object, and warns")
    void shouldIgnoreEmptyMount(final SystemErr systemErr) {
      final var json = DevcontainerBuilder.builder()
          .mounts(List.of(new Mount(null, null), new Mount(null, new MountObject(null, null, null))))
          .create();
      assertIterableEquals(List.of(), shellOptions(new DevcontainerOptions(), json).runtimeRunOptions);
      assertTrue(systemErr.getText().contains("mount"), systemErr.getText());
    }

    @Test
    @DisplayName("maps appPort alongside forwardPorts")
    void shouldMapAppPort() {
      final var json = DevcontainerBuilder.builder()
          .forwardPorts(List.of("8080"))
          .appPort(List.of("9090:90"))
          .create();
      assertIterableEquals(List.of("8080:8080", "9090:90"), shellOptions(new DevcontainerOptions(), json).ports);
    }

    @Test
    @DisplayName("maps containerUser to the remote user")
    void shouldMapContainerUser() {
      final var json = DevcontainerBuilder.builder().containerUser("node").create();
      assertEquals("node", shellOptions(new DevcontainerOptions(), json).remoteUser);
    }

    @Test
    @DisplayName("falls back to remoteUser when containerUser is absent")
    void shouldFallBackToRemoteUser() {
      final var json = DevcontainerBuilder.builder().remoteUser("vscode").create();
      assertEquals("vscode", shellOptions(new DevcontainerOptions(), json).remoteUser);
    }

    @Test
    @DisplayName("prefers containerUser over remoteUser")
    void shouldPreferContainerUser() {
      final var json = DevcontainerBuilder.builder().containerUser("node").remoteUser("vscode").create();
      assertEquals("node", shellOptions(new DevcontainerOptions(), json).remoteUser);
    }

    @Test
    @DisplayName("does not add the user to the run options")
    void shouldNotAddUserToRunOptions() {
      final var json = DevcontainerBuilder.builder().containerUser("node").create();
      assertIterableEquals(List.of(), shellOptions(new DevcontainerOptions(), json).runtimeRunOptions);
    }

    @Test
    @DisplayName("aligns the remote user UID by default")
    void shouldUpdateRemoteUserUidByDefault() {
      final var json = DevcontainerBuilder.builder().remoteUser("vscode").create();
      assertTrue(shellOptions(new DevcontainerOptions(), json).updateRemoteUserUID);
    }

    @Test
    @DisplayName("honors an explicit updateRemoteUserUID of false")
    void shouldHonorDisabledUpdateRemoteUserUid() {
      final var json = DevcontainerBuilder.builder().remoteUser("vscode").updateRemoteUserUID(false).create();
      assertFalse(shellOptions(new DevcontainerOptions(), json).updateRemoteUserUID);
    }

    @Test
    @DisplayName("maps init, privileged, capAdd and securityOpt to run options")
    void shouldMapSecurityAndInitFlags() {
      final var json = DevcontainerBuilder.builder()
          .init(true)
          .privileged(true)
          .capAdd(List.of("SYS_PTRACE"))
          .securityOpt(List.of("seccomp=unconfined"))
          .create();
      assertIterableEquals(
          List.of("--init", "--privileged", "--cap-add", "SYS_PTRACE", "--security-opt", "seccomp=unconfined"),
          shellOptions(new DevcontainerOptions(), json).runtimeRunOptions);
    }

    @Test
    @DisplayName("does not add init or privileged when false")
    void shouldNotAddInitOrPrivilegedWhenFalse() {
      final var json = DevcontainerBuilder.builder().init(false).privileged(false).create();
      assertIterableEquals(List.of(), shellOptions(new DevcontainerOptions(), json).runtimeRunOptions);
    }

    @Test
    @DisplayName("keeps runArgs ahead of the derived run options")
    void shouldKeepRunArgsFirst() {
      final var json = DevcontainerBuilder.builder().runArgs(List.of("--add-host=db:127.0.0.1")).init(true).create();
      assertIterableEquals(
          List.of("--add-host=db:127.0.0.1", "--init"),
          shellOptions(new DevcontainerOptions(), json).runtimeRunOptions);
    }

    @Test
    @DisplayName("maps build args, target and cacheFrom to build options")
    void shouldMapBuildOptions() {
      final var build = BuildBuilder.builder()
          .args(Map.of("VERSION", "1.2.3"))
          .target("builder")
          .cacheFrom(List.of("registry/image:cache"))
          .create();
      final var json = DevcontainerBuilder.builder().build(build).create();
      assertIterableEquals(
          List.of("--build-arg", "VERSION=1.2.3", "--target", "builder", "--cache-from", "registry/image:cache"),
          shellOptions(new DevcontainerOptions(), json).runtimeBuildOptions);
    }

  }

  @Nested
  @DisplayName("compose options")
  class ComposeOptionsMapper {

    @Test
    @DisplayName("returns non-null values")
    void shouldReturnNonNullValues() {
      assertNotNull(composeOptions(new DevcontainerOptions(), DevcontainerBuilder.builder().create(), Paths.get(".")));
    }

    @Test
    @DisplayName("resolves the dockerComposeFile against the devcontainer.json directory")
    void shouldMapDockerComposeFile() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().dockerComposeFile(List.of("your-compose.yml")).create();
      // The compose file must be resolved relative to the devcontainer.json's directory rather than
      // relativized against the file itself; the expected path is built the same way so the assertion
      // holds on any OS (drive letters and separators differ on Windows).
      final var devcontainerJson = Paths.get("project", ".devcontainer", "devcontainer.json");
      final var expected = devcontainerJson.toAbsolutePath().getParent().resolve("your-compose.yml").toString();

      // when
      final var composeOptions = composeOptions(options, json, devcontainerJson);

      // then
      assertEquals(expected, composeOptions.file.get(0));
    }

    @Test
    @DisplayName("maps the service field")
    void shouldMapService() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().service("some-service").create();

      // when
      final var composeOptions = composeOptions(options, json, Paths.get("."));

      // then
      assertEquals(json.service(), composeOptions.service);
    }

    @Test
    @DisplayName("--interactive is set to true")
    void shouldEnableInteractiveMode() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().create();

      // when
      final var composeOptions = composeOptions(options, json, Paths.get("."));

      // then
      assertTrue(composeOptions.interactive);
    }

    @Test
    @DisplayName("overrides the command by default when the definition is silent")
    void shouldOverrideCommandByDefault() {
      assertTrue(composeOptions(new DevcontainerOptions(), DevcontainerBuilder.builder().create(), Paths.get(".")).overrideCommand);
    }

    @Test
    @DisplayName("honours overrideCommand=false from the devcontainer definition")
    void shouldRespectOverrideCommandFalse() {
      final var json = DevcontainerBuilder.builder().overrideCommand(false).create();
      assertFalse(composeOptions(new DevcontainerOptions(), json, Paths.get(".")).overrideCommand);
    }

    @Test
    @DisplayName("keeps the services running on exit for shutdownAction none")
    void shouldKeepServicesRunningForShutdownActionNone() {
      final var json = DevcontainerBuilder.builder().shutdownAction(ShutdownAction.none).create();
      assertTrue(composeOptions(new DevcontainerOptions(), json, Paths.get(".")).keepRunningOnExit);
    }

    @Test
    @DisplayName("maps runServices to additional services to bring up")
    void shouldMapRunServices() {
      final var json = DevcontainerBuilder.builder().runServices(List.of("db", "cache")).create();
      assertIterableEquals(List.of("db", "cache"),
          composeOptions(new DevcontainerOptions(), json, Paths.get(".")).runServices);
    }

  }

}
