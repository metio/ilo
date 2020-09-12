/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
      assertNotNull(shellOptions(new DevcontainerOptions(), new DevcontainerJson()));
    }

    @Test
    @DisplayName("maps the image field")
    void shouldMapImage() {
      // given
      final var options = new DevcontainerOptions();
      final var json = new DevcontainerJson();
      json.image = "example:123";

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertEquals(json.image, shellOptions.image);
    }

    @Test
    @DisplayName("maps the dockerFile field")
    void shouldMapDockerfile() {
      // given
      final var options = new DevcontainerOptions();
      final var json = new DevcontainerJson();
      json.dockerFile = "some.dockerfile";

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertEquals(json.dockerFile, shellOptions.dockerfile);
    }

    @Test
    @DisplayName("maps the context field")
    void shouldMapContext() {
      // given
      final var options = new DevcontainerOptions();
      final var json = new DevcontainerJson();
      json.context = ".";

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertEquals(json.context, shellOptions.context);
    }

    @Test
    @DisplayName("maps the build.dockerFile field")
    void shouldMapBuildDockerfile() {
      // given
      final var options = new DevcontainerOptions();
      final var json = new DevcontainerJson();
      json.build = new DevcontainerJson.Build();
      json.build.dockerFile = "some.dockerfile";

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertEquals(json.build.dockerFile, shellOptions.dockerfile);
    }

    @Test
    @DisplayName("maps the build.context field")
    void shouldMapBuildContext() {
      // given
      final var options = new DevcontainerOptions();
      final var json = new DevcontainerJson();
      json.build = new DevcontainerJson.Build();
      json.build.context = ".";

      // when
      final var shellOptions = shellOptions(options, json);

      // then
      assertEquals(json.build.context, shellOptions.context);
    }

  }

  @Nested
  @DisplayName("compose options")
  class ComposeOptionsMapper {

    @Test
    @DisplayName("returns non-null values")
    void shouldReturnNonNullValues() {
      assertNotNull(composeOptions(new DevcontainerOptions(), new DevcontainerJson()));
    }

    @Test
    @DisplayName("maps the dockerComposeFile field")
    void shouldMapDockerComposefile() {
      // given
      final var options = new DevcontainerOptions();
      final var json = new DevcontainerJson();
      json.dockerComposeFile = "your-compose.yml";

      // when
      final var composeOptions = composeOptions(options, json);

      // then
      assertEquals(json.dockerComposeFile, composeOptions.file);
    }

    @Test
    @DisplayName("maps the service field")
    void shouldMapService() {
      // given
      final var options = new DevcontainerOptions();
      final var json = new DevcontainerJson();
      json.service = "some-service";

      // when
      final var composeOptions = composeOptions(options, json);

      // then
      assertEquals(json.service, composeOptions.service);
    }

  }

}
