/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.devfile;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.dataformat.yaml.YAMLMapper;
import wtf.metio.ilo.errors.DevfileYamlMissingException;
import wtf.metio.ilo.errors.JsonParsingException;
import wtf.metio.ilo.utils.Streams;

import java.nio.file.Path;
import java.util.List;

final class DevfileYamlParser {

  static Path findDevfile(final Path baseDirectory, final List<String> locations) {
    return Streams.findFirst(baseDirectory, locations).orElseThrow(DevfileYamlMissingException::new);
  }

  static DevfileYaml parseDevfile(final Path devfile) {
    final var mapper = YAMLMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
    try {
      return mapper.readValue(devfile.toFile(), DevfileYaml.class);
    } catch (final JacksonException exception) {
      throw new JsonParsingException(exception);
    }
  }

  private DevfileYamlParser() {
    // utility class
  }


}
