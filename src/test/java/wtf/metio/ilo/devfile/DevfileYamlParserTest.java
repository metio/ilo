/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package wtf.metio.ilo.devfile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static wtf.metio.ilo.devfile.DevfileYamlParser.findDevfile;
import static wtf.metio.ilo.test.TestResources.testResources;

class DevfileYamlParserTest {

  private static final List<String> DEFAULT_LOCATIONS = List.of(".devfile.yaml", "devfile.yaml");

  @Test
  @DisplayName("can parse example/devfile.yaml")
  void shouldParseYaml() {
    final var devfile = DevfileYamlParser.parseDevfile(findYamlIn("example"));
    assertAll("devfile",
        () -> assertEquals("2.2.0", devfile.schemaVersion, "schemaVersion"),
        () -> assertEquals("devfile-api", devfile.metadata.name, "metadata.name"),
        () -> assertEquals("1.2.3", devfile.metadata.version, "metadata.version"));
  }

  @Test
  @DisplayName("can parse variable/devfile.yaml")
  void shouldParseYaml2() {
    final var devfile = DevfileYamlParser.parseDevfile(findYamlIn("variable"));
    assertAll("devfile",
        () -> assertEquals("2.2.0", devfile.schemaVersion, "schemaVersion"),
        () -> assertEquals("java-maven", devfile.metadata.name, "metadata.name"),
        () -> assertEquals("1.1.1", devfile.metadata.version, "metadata.version"),
        () -> assertEquals("11", devfile.variables.get("javaVersion"), "variables.javaVersion"));
  }

  @Test
  @DisplayName("can parse plain/devfile.yaml")
  void shouldParseYaml3() {
    final var devfile = DevfileYamlParser.parseDevfile(findYamlIn("plain"));
    assertAll("devfile",
        () -> assertEquals("2.2.0", devfile.schemaVersion, "schemaVersion"),
        () -> assertEquals("devfile-sample", devfile.metadata.name, "metadata.name"),
        () -> assertEquals("2.1.0", devfile.metadata.version, "metadata.version"));
  }

  @Test
  @DisplayName("can parse openshift/devfile.yaml")
  void shouldParseYaml4() {
    final var devfile = DevfileYamlParser.parseDevfile(findYamlIn("openshift"));
    assertAll("devfile",
        () -> assertEquals("2.1.0", devfile.schemaVersion, "schemaVersion"),
        () -> assertEquals("devfile-openshift", devfile.metadata.name, "metadata.name"),
        () -> assertEquals("1.2.3", devfile.metadata.version, "metadata.version"));
  }

  @Test
  @DisplayName("can parse kubernetes-inline/devfile.yaml")
  void shouldParseYaml5() {
    final var devfile = DevfileYamlParser.parseDevfile(findYamlIn("kubernetes-inline"));
    assertAll("devfile",
        () -> assertEquals("2.2.2", devfile.schemaVersion, "schemaVersion"),
        () -> assertEquals("devfile-kubernetes-inline", devfile.metadata.name, "metadata.name"),
        () -> assertEquals("1.2.3", devfile.metadata.version, "metadata.version"));
  }

  @Test
  @DisplayName("can parse container/devfile.yaml")
  void shouldParseYaml6() {
    final var devfile = DevfileYamlParser.parseDevfile(findYamlIn("container"));
    assertAll("devfile",
        () -> assertEquals("2.2.0", devfile.schemaVersion, "schemaVersion"),
        () -> assertEquals("devfile-container", devfile.metadata.name, "metadata.name"),
        () -> assertEquals("1.2.3", devfile.metadata.version, "metadata.version"),
        () -> assertEquals(1, devfile.components.size(), "components.size"),
        () -> assertEquals("maven", devfile.components.get(0).name, "component.name"),
        () -> assertEquals("eclipse/maven-jdk8:latest", devfile.components.get(0).container.image, "container.image"),
        () -> assertEquals("ENV_VAR", devfile.components.get(0).container.env.get(0).name, "env.name"),
        () -> assertEquals("maven-server", devfile.components.get(0).container.endpoints.get(0).name, "endpoint.name"),
        () -> assertEquals("mavenrepo", devfile.components.get(0).container.volumeMounts.get(0).name, "volumeMount.name"),
        () -> assertEquals("/root/.m2", devfile.components.get(0).container.volumeMounts.get(0).path, "volumeMount.path"));
  }

  @Test
  @DisplayName("can parse image/devfile.yaml")
  void shouldParseYaml7() {
    final var devfile = DevfileYamlParser.parseDevfile(findYamlIn("image"));
    assertAll("devfile",
        () -> assertEquals("2.2.0", devfile.schemaVersion, "schemaVersion"),
        () -> assertEquals("devfile-image", devfile.metadata.name, "metadata.name"),
        () -> assertEquals("1.2.3", devfile.metadata.version, "metadata.version"),
        () -> assertEquals(1, devfile.components.size(), "components.size"),
        () -> assertEquals("outerloop-build", devfile.components.get(0).name, "component.name"),
        () -> assertEquals("python-image:latest", devfile.components.get(0).image.imageName, "image.imageName"),
        () -> assertEquals(".", devfile.components.get(0).image.dockerfile.buildContext, "image.dockerfile.buildContext"),
        () -> assertEquals("docker/Dockerfile", devfile.components.get(0).image.dockerfile.uri, "image.dockerfile.uri"));
  }

  @Test
  @DisplayName("can parse volume/devfile.yaml")
  void shouldParseYaml8() {
    final var devfile = DevfileYamlParser.parseDevfile(findYamlIn("volume"));
    assertAll("devfile",
        () -> assertEquals("2.2.0", devfile.schemaVersion, "schemaVersion"),
        () -> assertEquals("devfile-volume", devfile.metadata.name, "metadata.name"),
        () -> assertEquals("1.2.3", devfile.metadata.version, "metadata.version"),
        () -> assertEquals(2, devfile.components.size(), "components.size"),
        () -> assertEquals("mydevfile", devfile.components.get(0).name, "component.name"),
        () -> assertEquals("cache", devfile.components.get(1).name, "component.name"),
        () -> assertEquals("2Gi", devfile.components.get(1).volume.size, "volume.size"));
  }

  @Test
  @DisplayName("can parse image-registry/devfile.yaml")
  void shouldParseYaml9() {
    final var devfile = DevfileYamlParser.parseDevfile(findYamlIn("image-registry"));
    assertAll("devfile",
        () -> assertEquals("2.2.0", devfile.schemaVersion, "schemaVersion"),
        () -> assertEquals("devfile-image", devfile.metadata.name, "metadata.name"),
        () -> assertEquals("1.2.3", devfile.metadata.version, "metadata.version"),
        () -> assertEquals(1, devfile.components.size(), "components.size"),
        () -> assertEquals("outerloop-build", devfile.components.get(0).name, "component.name"),
        () -> assertEquals("python-image:latest", devfile.components.get(0).image.imageName, "image.imageName"),
        () -> assertEquals(".", devfile.components.get(0).image.dockerfile.buildContext, "image.dockerfile.buildContext"),
        () -> assertEquals("some-id", devfile.components.get(0).image.dockerfile.devfileRegistry.id, "image.dockerfile.devfileRegistry.id"),
        () -> assertEquals("https://example.com", devfile.components.get(0).image.dockerfile.devfileRegistry.registryUrl, "image.dockerfile.devfileRegistry.registryUrl"));
  }

  @Test
  @DisplayName("can parse image-git/devfile.yaml")
  void shouldParseYaml10() {
    final var devfile = DevfileYamlParser.parseDevfile(findYamlIn("image-git"));
    assertAll("devfile",
        () -> assertEquals("2.2.0", devfile.schemaVersion, "schemaVersion"),
        () -> assertEquals("devfile-image", devfile.metadata.name, "metadata.name"),
        () -> assertEquals("1.2.3", devfile.metadata.version, "metadata.version"),
        () -> assertEquals(1, devfile.components.size(), "components.size"),
        () -> assertEquals("outerloop-build", devfile.components.get(0).name, "component.name"),
        () -> assertEquals("python-image:latest", devfile.components.get(0).image.imageName, "image.imageName"),
        () -> assertEquals(".", devfile.components.get(0).image.dockerfile.buildContext, "image.dockerfile.buildContext"),
        () -> assertEquals("origin", devfile.components.get(0).image.dockerfile.git.checkoutFrom.remote, "image.dockerfile.git.checkoutFrom.remote"),
        () -> assertEquals("HEAD", devfile.components.get(0).image.dockerfile.git.checkoutFrom.revision, "image.dockerfile.git.checkoutFrom.revision"));
  }

  private Path findYamlIn(final String testDirectory) {
    return findDevfile(testResources(DevfileYamlParser.class).resolve(testDirectory), DEFAULT_LOCATIONS);
  }

}
