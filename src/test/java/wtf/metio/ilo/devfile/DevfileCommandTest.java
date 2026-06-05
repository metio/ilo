/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.devfile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import picocli.CommandLine;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;
import wtf.metio.ilo.devfile.DevfileYaml.Component;
import wtf.metio.ilo.devfile.DevfileYaml.Container;
import wtf.metio.ilo.devfile.DevfileYaml.Dockerfile;
import wtf.metio.ilo.devfile.DevfileYaml.Env;
import wtf.metio.ilo.devfile.DevfileYaml.Image;
import wtf.metio.ilo.shell.ShellOptions;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static wtf.metio.ilo.test.TestResources.testResources;

@DisplayName("DevfileCommand")
@ExtendWith(SystemStubsExtension.class)
class DevfileCommandTest {

  @Test
  @DisplayName("supports a component that defines a predefined image")
  void shouldSupportPredefinedImages() {
    final var devfile = devfile(containerComponent("container", "docker.io/library/maven:latest"));
    assertTrue(DevfileCommand.hasSupportedDevfileConfiguration(devfile, "container"));
  }

  @Test
  @DisplayName("supports a component that builds from a local dockerfile")
  void shouldSupportLocalDockerfile() {
    final var devfile = devfile(dockerfileComponent("image", "image:latest", "./folder/Containerfile"));
    assertTrue(DevfileCommand.hasSupportedDevfileConfiguration(devfile, "image"));
  }

  @Test
  @DisplayName("rejects a dockerfile without a uri")
  void shouldNotSupportDockerfileWithoutUri() {
    final var devfile = devfile(dockerfileComponent("image", "image:latest", null));
    assertFalse(DevfileCommand.hasSupportedDevfileConfiguration(devfile, "image"));
  }

  @Test
  @DisplayName("rejects a component whose name does not match")
  void shouldNotSupportUnknownComponent() {
    final var devfile = devfile(containerComponent("container", "docker.io/library/maven:latest"));
    assertFalse(DevfileCommand.hasSupportedDevfileConfiguration(devfile, "other"));
  }

  @Test
  @DisplayName("supports the first supported component when no component is requested")
  void shouldSupportFirstComponentWhenNoneRequested() {
    final var devfile = devfile(containerComponent("container", "docker.io/library/maven:latest"));
    assertTrue(DevfileCommand.hasSupportedDevfileConfiguration(devfile, null));
  }

  @Test
  @DisplayName("maps a predefined image to shell options")
  void shouldMapPredefinedImages() {
    final var container = new Container("docker.io/library/maven:latest", true, "/workspace",
        List.of("sleep"), List.of("infinity"), List.of(new Env("FOO", "bar")));
    final var devfile = devfile(new Component("container", container, emptyImage()));

    final var shellOptions = DevfileCommand.mapOptions(new DevfileOptions(), devfile);

    assertAll("predefined image options",
        () -> assertEquals("docker.io/library/maven:latest", shellOptions.image, "image"),
        () -> assertTrue(shellOptions.mountProjectDir, "mountProjectDir"),
        () -> assertEquals("/workspace", shellOptions.workingDir, "workingDir"),
        () -> assertEquals(List.of("FOO=bar"), shellOptions.variables, "variables"),
        () -> assertEquals(List.of("sleep", "infinity"), shellOptions.commands, "commands"));
  }

  @Test
  @DisplayName("drops env entries with a null name or value instead of emitting null=… or …=null")
  void shouldDropIncompleteEnv() {
    final var env = Arrays.asList(
        new Env("KEEP", "value"),
        new Env("NO_VALUE", null),
        new Env(null, "orphan"));
    final var container = new Container("maven:latest", false, null, List.of(), List.of(), env);
    final var devfile = devfile(new Component("container", container, emptyImage()));

    final var shellOptions = DevfileCommand.mapOptions(new DevfileOptions(), devfile);

    assertEquals(List.of("KEEP=value"), shellOptions.variables);
  }

  @Test
  @DisplayName("maps a local dockerfile to shell options")
  void shouldMapLocalDockerfile() {
    final var devfile = devfile(dockerfileComponent("image", "python:latest", "docker/Dockerfile"));

    final var shellOptions = DevfileCommand.mapOptions(new DevfileOptions(), devfile);

    assertAll("local dockerfile options",
        () -> assertTrue(shellOptions.mountProjectDir, "mountProjectDir"),
        () -> assertEquals("python:latest", shellOptions.image, "image"),
        () -> assertEquals("docker/Dockerfile", shellOptions.containerfile, "containerfile"),
        () -> assertEquals(".", shellOptions.context, "context"));
  }

  @Test
  @DisplayName("maps the first supported component when no component is requested")
  void shouldMapFirstSupportedComponentWhenNoneRequested() {
    final var devfile = devfile(
        containerComponent("first", "first:latest"),
        containerComponent("second", "second:latest"));

    final var shellOptions = DevfileCommand.mapOptions(new DevfileOptions(), devfile);

    assertEquals("first:latest", shellOptions.image);
  }

  @Test
  @DisplayName("maps the requested component rather than the first supported one")
  void shouldMapRequestedComponent() {
    final var options = new DevfileOptions();
    options.component = "second";
    final var devfile = devfile(
        containerComponent("first", "first:latest"),
        containerComponent("second", "second:latest"));

    final var shellOptions = DevfileCommand.mapOptions(options, devfile);

    assertEquals("second:latest", shellOptions.image);
  }

  @Test
  @DisplayName("opens an interactive shell for both component kinds")
  void shouldOpenInteractiveShell() {
    final var predefined = DevfileCommand.mapOptions(new DevfileOptions(),
        devfile(containerComponent("container", "maven:latest")));
    final var dockerfile = DevfileCommand.mapOptions(new DevfileOptions(),
        devfile(dockerfileComponent("image", "image:latest", "Dockerfile")));
    assertAll(
        () -> assertTrue(predefined.interactive, "predefined image"),
        () -> assertTrue(dockerfile.interactive, "local dockerfile"));
  }

  @Test
  @DisplayName("copies runtime options onto the shell options")
  void shouldCopyRuntimeOptions() {
    final var options = new DevfileOptions();
    options.debug = true;
    options.pull = true;
    options.removeImage = true;
    options.runtimeOptions = List.of("--privileged");

    final var shellOptions = DevfileCommand.mapOptions(options,
        devfile(containerComponent("container", "maven:latest")));

    assertAll("copied options",
        () -> assertTrue(shellOptions.debug, "debug"),
        () -> assertTrue(shellOptions.pull, "pull"),
        () -> assertTrue(shellOptions.removeImage, "removeImage"),
        () -> assertEquals(List.of("--privileged"), shellOptions.runtimeOptions, "runtimeOptions"));
  }

  @Test
  @DisplayName("fails to map a devfile without any supported component")
  void shouldFailWithoutSupportedComponent() {
    final var devfile = devfile(new Component("volume", emptyContainer(), emptyImage()));
    assertThrows(NoSuchElementException.class, () -> DevfileCommand.mapOptions(new DevfileOptions(), devfile));
  }

  @Test
  @DisplayName("runs the shell options derived from a supported devfile")
  void shouldRunSupportedDevfile(final SystemProperties properties) throws Exception {
    final var captured = new AtomicReference<ShellOptions>();
    final var command = new DevfileCommand(shellOptions -> {
      captured.set(shellOptions);
      return 42;
    });
    command.options = optionsFor("maven");
    properties.set("user.dir", resourceDir("container"));

    final var exitCode = command.call();

    assertAll("supported devfile",
        () -> assertEquals(42, exitCode, "exit code from the shell runner"),
        () -> assertEquals("eclipse/maven-jdk8:latest", captured.get().image, "mapped image"));
  }

  @Test
  @DisplayName("opens a shell for the first supported component when none is requested")
  void shouldRunFirstSupportedComponentWhenNoneRequested(final SystemProperties properties) throws Exception {
    final var captured = new AtomicReference<ShellOptions>();
    final var command = new DevfileCommand(shellOptions -> {
      captured.set(shellOptions);
      return 42;
    });
    command.options = optionsFor(null);
    properties.set("user.dir", resourceDir("container"));

    final var exitCode = command.call();

    assertAll("default component",
        () -> assertEquals(42, exitCode, "exit code from the shell runner"),
        () -> assertEquals("eclipse/maven-jdk8:latest", captured.get().image, "mapped image"));
  }

  @Test
  @DisplayName("reports usage for a devfile without a supported component")
  void shouldReportUsageForUnsupportedDevfile(final SystemProperties properties) throws Exception {
    final var command = new DevfileCommand(shellOptions -> fail("the shell must not be opened"));
    command.options = optionsFor("maven");
    properties.set("user.dir", resourceDir("plain"));

    assertEquals(CommandLine.ExitCode.USAGE, command.call());
  }

  private static DevfileOptions optionsFor(final String component) {
    final var options = new DevfileOptions();
    options.locations = List.of("devfile.yaml", ".devfile.yaml");
    options.component = component;
    return options;
  }

  private static String resourceDir(final String name) {
    return testResources(DevfileYamlParser.class).resolve(name).toAbsolutePath().toString();
  }

  private static DevfileYaml devfile(final Component... components) {
    return new DevfileYaml(List.of(components));
  }

  private static Component containerComponent(final String name, final String image) {
    return new Component(name, new Container(image, false, null, List.of(), List.of(), List.of()), emptyImage());
  }

  private static Component dockerfileComponent(final String name, final String imageName, final String uri) {
    return new Component(name, emptyContainer(), new Image(imageName, new Dockerfile(uri, ".")));
  }

  private static Container emptyContainer() {
    return new Container(null, false, null, List.of(), List.of(), List.of());
  }

  private static Image emptyImage() {
    return new Image(null, new Dockerfile(null, null));
  }

}
