/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devfile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Devfile")
class DevfileTest {

  @Test
  void shouldSupportPredefinedImages() {
    final var devfile = new DevfileYaml();
    final var container = new DevfileYaml.Container();
    container.image = "docker.io/library/maven:latest";
    final var component = new DevfileYaml.Component();
    component.name = "container";
    component.container = container;
    devfile.components = List.of(component);

    assertTrue(Devfile.hasSupportedDevfileConfiguration(devfile, "container"));
  }

  @Test
  void shouldSupportLocalDockerfile() {
    final var devfile = new DevfileYaml();
    final var image = new DevfileYaml.Image();
    final var dockerfile = new DevfileYaml.Dockerfile();
    dockerfile.uri = "./folder/Containerfile";
    image.dockerfile = dockerfile;
    final var component = new DevfileYaml.Component();
    component.name = "image";
    component.image = image;
    devfile.components = List.of(component);

    assertTrue(Devfile.hasSupportedDevfileConfiguration(devfile, "image"));
  }

}
