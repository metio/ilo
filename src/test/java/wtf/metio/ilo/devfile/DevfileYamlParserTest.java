/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.devfile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.dataformat.yaml.YAMLMapper;
import wtf.metio.ilo.errors.DevfileYamlMissingException;
import wtf.metio.ilo.errors.JsonParsingException;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static wtf.metio.ilo.devfile.DevfileYamlParser.findDevfile;
import static wtf.metio.ilo.devfile.DevfileYamlParser.parseDevfile;
import static wtf.metio.ilo.devfile.DevfileYamlParser.toDevfile;
import static wtf.metio.ilo.test.TestResources.testResources;

@DisplayName("DevfileYamlParser")
class DevfileYamlParserTest {

  private static final List<String> DEFAULT_LOCATIONS = List.of(".devfile.yaml", "devfile.yaml");

  @Test
  @DisplayName("coerces non-string command/args elements and drops nulls")
  void shouldCoerceAndDropArrayElements() {
    final var container = parseDevfile(findYamlIn("coerced-command")).components().get(0).container();
    assertAll("coerced elements",
        () -> assertEquals(List.of("echo", "8080"), container.command(), "container.command"),
        () -> assertEquals(List.of("--retries", "3"), container.args(), "container.args"));
  }

  @Test
  @DisplayName("extracts the container component from container/devfile.yaml")
  void shouldParseContainer() {
    final var component = parseDevfile(findYamlIn("container")).components().get(0);
    final var container = component.container();
    assertAll("container component",
        () -> assertEquals("maven", component.name(), "name"),
        () -> assertEquals("eclipse/maven-jdk8:latest", container.image(), "container.image"),
        // The fixture omits mountSources; the devfile spec default is true (sources are mounted).
        () -> assertTrue(container.mountSources(), "container.mountSources defaults to true"),
        () -> assertEquals(List.of("tail"), container.command(), "container.command"),
        () -> assertEquals(List.of("-f", "/dev/null"), container.args(), "container.args"),
        () -> assertEquals("ENV_VAR", container.env().get(0).name(), "env.name"),
        () -> assertEquals("value", container.env().get(0).value(), "env.value"));
  }

  @Test
  @DisplayName("extracts the local-dockerfile image component from image/devfile.yaml")
  void shouldParseImage() {
    final var component = parseDevfile(findYamlIn("image")).components().get(0);
    final var image = component.image();
    assertAll("image component",
        () -> assertEquals("outerloop-build", component.name(), "name"),
        () -> assertEquals("python-image:latest", image.imageName(), "image.imageName"),
        () -> assertEquals("docker/Dockerfile", image.dockerfile().uri(), "dockerfile.uri"),
        () -> assertEquals(".", image.dockerfile().buildContext(), "dockerfile.buildContext"),
        () -> assertEquals(List.of("MY_ENV=/home/path"), image.dockerfile().args(), "dockerfile.args"));
  }

  @Test
  @DisplayName("leaves the dockerfile uri empty when the image is built from git")
  void shouldParseImageWithoutDockerfileUri() {
    final var component = parseDevfile(findYamlIn("image-git")).components().get(0);
    assertAll("git-built image component",
        () -> assertEquals("python-image:latest", component.image().imageName(), "image.imageName"),
        () -> assertNull(component.image().dockerfile().uri(), "dockerfile.uri"));
  }

  @Test
  @DisplayName("extracts every component from volume/devfile.yaml")
  void shouldParseMultipleComponents() {
    final var components = parseDevfile(findYamlIn("volume")).components();
    assertAll("components",
        () -> assertEquals(2, components.size(), "size"),
        () -> assertEquals("golang", components.get(0).container().image(), "first.container.image"),
        () -> assertTrue(components.get(0).container().mountSources(), "first.container.mountSources"),
        () -> assertEquals(List.of("sleep", "infinity"), components.get(0).container().command(), "first.container.command"),
        () -> assertEquals("cache", components.get(1).name(), "second.name"),
        () -> assertNull(components.get(1).container().image(), "second.container.image"));
  }

  @Test
  @DisplayName("yields no components when the devfile declares none")
  void shouldParseWithoutComponents() {
    assertEquals(List.of(), parseDevfile(findYamlIn("plain")).components(), "components");
  }

  @Test
  @DisplayName("represents a component that is neither container nor image with empty values")
  void shouldParseUnsupportedComponentKind() {
    final var component = parseDevfile(findYamlIn("kubernetes-inline")).components().get(0);
    assertAll("unsupported component",
        () -> assertEquals("myk8deploy", component.name(), "name"),
        () -> assertNull(component.container().image(), "container.image"),
        () -> assertNull(component.image().dockerfile().uri(), "dockerfile.uri"));
  }

  @Test
  @DisplayName("treats a null scalar the same as an absent one")
  void shouldTolerateNullScalars() {
    final var component = toDevfile(tree("""
        components:
          - name: null
            container:
              image:
        """)).components().get(0);
    assertAll("null scalars",
        () -> assertNull(component.name(), "name"),
        () -> assertNull(component.container().image(), "container.image"));
  }

  @Test
  @DisplayName("reports a parsing failure for a malformed devfile")
  void shouldRejectMalformedYaml() {
    assertThrows(JsonParsingException.class, () -> parseDevfile(findYamlIn("malformed")));
  }

  @Test
  @DisplayName("reports a missing devfile when no location matches")
  void shouldReportMissingDevfile() {
    final var directory = testResources(DevfileYamlParser.class).resolve("does-not-exist");
    assertThrows(DevfileYamlMissingException.class, () -> findDevfile(directory, DEFAULT_LOCATIONS));
  }

  private static JsonNode tree(final String yaml) {
    return YAMLMapper.builder().build().readTree(yaml);
  }

  private static Path findYamlIn(final String testDirectory) {
    return findDevfile(testResources(DevfileYamlParser.class).resolve(testDirectory), DEFAULT_LOCATIONS);
  }

}
