/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wtf.metio.devcontainer.BuildBuilder;
import wtf.metio.devcontainer.DevcontainerBuilder;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static wtf.metio.ilo.devcontainer.DevcontainerOptionsMapper.composeOptions;
import static wtf.metio.ilo.devcontainer.DevcontainerOptionsMapper.shellOptions;

@DisplayName("DevcontainerOptionsMapper")
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
    @DisplayName("maps the dockerComposeFile field")
    void shouldMapDockerComposeFile() {
      // given
      final var options = new DevcontainerOptions();
      final var json = DevcontainerBuilder.builder().dockerComposeFile(List.of("your-compose.yml")).create();

      // when
      final var composeOptions = composeOptions(options, json, Paths.get("."));

      // then
      assertTrue(composeOptions.file.get(0).endsWith("your-compose.yml"));
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

  }

}
