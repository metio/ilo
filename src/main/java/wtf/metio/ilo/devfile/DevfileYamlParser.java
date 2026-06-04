/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.devfile;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.dataformat.yaml.YAMLMapper;
import wtf.metio.ilo.devfile.DevfileYaml.Component;
import wtf.metio.ilo.devfile.DevfileYaml.Container;
import wtf.metio.ilo.devfile.DevfileYaml.Dockerfile;
import wtf.metio.ilo.devfile.DevfileYaml.Env;
import wtf.metio.ilo.devfile.DevfileYaml.Image;
import wtf.metio.ilo.errors.DevfileYamlMissingException;
import wtf.metio.ilo.errors.JsonParsingException;
import wtf.metio.ilo.utils.Streams;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

final class DevfileYamlParser {

  private static final YAMLMapper YAML = YAMLMapper.builder().build();

  static Path findDevfile(final Path baseDirectory, final List<String> locations) {
    return Streams.findFirst(baseDirectory, locations).orElseThrow(DevfileYamlMissingException::new);
  }

  static DevfileYaml parseDevfile(final Path devfile) {
    try {
      return toDevfile(YAML.readTree(devfile.toFile()));
    } catch (final JacksonException exception) {
      throw new JsonParsingException(exception);
    }
  }

  // Reads the model straight out of the parsed YAML tree by field name. Walking the tree keeps the
  // mapping reflection-free, so the native image needs no per-class metadata for these types.
  static DevfileYaml toDevfile(final JsonNode root) {
    final var components = new ArrayList<Component>();
    for (final var component : root.path("components")) {
      components.add(component(component));
    }
    return new DevfileYaml(components);
  }

  private static Component component(final JsonNode node) {
    return new Component(text(node, "name"), container(node.path("container")), image(node.path("image")));
  }

  private static Container container(final JsonNode node) {
    return new Container(
        text(node, "image"),
        node.path("mountSources").booleanValue(false),
        text(node, "sourceMapping"),
        strings(node.path("command")),
        strings(node.path("args")),
        envs(node.path("env")));
  }

  private static Image image(final JsonNode node) {
    return new Image(text(node, "imageName"), dockerfile(node.path("dockerfile")));
  }

  private static Dockerfile dockerfile(final JsonNode node) {
    return new Dockerfile(text(node, "uri"), text(node, "buildContext"));
  }

  private static List<Env> envs(final JsonNode node) {
    final var envs = new ArrayList<Env>();
    for (final var env : node) {
      envs.add(new Env(text(env, "name"), text(env, "value")));
    }
    return envs;
  }

  private static List<String> strings(final JsonNode node) {
    final var values = new ArrayList<String>();
    for (final var value : node) {
      values.add(value.stringValue(null));
    }
    return values;
  }

  // Returns null for a missing, null, or non-string field, so an absent value and an explicit null
  // are indistinguishable to callers.
  private static String text(final JsonNode node, final String field) {
    return node.path(field).stringValue(null);
  }

  private DevfileYamlParser() {
    // utility class
  }

}
