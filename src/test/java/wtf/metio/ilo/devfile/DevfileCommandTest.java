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

@DisplayName("DevfileCommand")
class DevfileCommandTest {

  @Test
  void shouldSupportPredefinedImages() {
    final var devfile = new DevfileYaml();
    final var container = new DevfileYaml.Container();
    container.image = "docker.io/library/maven:latest";
    final var component = new DevfileYaml.Component();
    component.name = "container";
    component.container = container;
    devfile.components = List.of(component);

    assertTrue(DevfileCommand.hasSupportedDevfileConfiguration(devfile, "container"));
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

    assertTrue(DevfileCommand.hasSupportedDevfileConfiguration(devfile, "image"));
  }

  @Test
  void shouldNotSupportGitDockerfile() {
    final var devfile = new DevfileYaml();
    final var image = new DevfileYaml.Image();
    final var dockerfile = new DevfileYaml.Dockerfile();
    dockerfile.git = new DevfileYaml.Git();
    dockerfile.git.checkoutFrom = new DevfileYaml.CheckoutFrom();
    dockerfile.git.checkoutFrom.remote = "origin";
    dockerfile.git.checkoutFrom.revision = "HEAD";
    image.dockerfile = dockerfile;
    final var component = new DevfileYaml.Component();
    component.name = "image";
    component.image = image;
    devfile.components = List.of(component);

    assertFalse(DevfileCommand.hasSupportedDevfileConfiguration(devfile, "image"));
  }

  @Test
  void shouldMapPredefinedImages() {
    final var devfile = new DevfileYaml();
    final var container = new DevfileYaml.Container();
    container.image = "docker.io/library/maven:latest";
    final var component = new DevfileYaml.Component();
    component.name = "container";
    component.container = container;
    devfile.components = List.of(component);
    final var options = new DevfileOptions();
    final var shellOptions = DevfileCommand.mapOptions(options, devfile);

    assertEquals("docker.io/library/maven:latest", shellOptions.image);
  }

}
