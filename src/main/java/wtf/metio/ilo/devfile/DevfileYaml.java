/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.devfile;

import java.util.List;

/**
 * The slice of the <a href="https://devfile.io/docs/2.2.0/devfile-schema">devfile schema v2.2.0</a>
 * that a build environment is derived from: the components and the container or image fields the
 * shell consumes. The value is assembled from the parsed YAML tree, so it needs no reflection
 * metadata to be produced in the native image.
 *
 * <p>Every nested element is non-null: a component that is neither a container nor an image
 * component still carries empty {@link Container} and {@link Image} values, and absent lists are
 * empty rather than {@code null}. Callers therefore decide what a component <em>is</em> from its
 * populated fields ({@link Container#image()}, {@link Dockerfile#uri()}), not from null checks.
 */
record DevfileYaml(List<Component> components) {

  record Component(String name, Container container, Image image) {
  }

  record Container(
      String image,
      boolean mountSources,
      String sourceMapping,
      List<String> command,
      List<String> args,
      List<Env> env) {
  }

  record Env(String name, String value) {
  }

  record Image(String imageName, Dockerfile dockerfile) {
  }

  record Dockerfile(String uri, String buildContext) {
  }

}
