/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.devfile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import wtf.metio.ilo.errors.DevfileYamlMissingException;
import wtf.metio.ilo.errors.JsonParsingException;
import wtf.metio.ilo.errors.RuntimeIOException;
import wtf.metio.ilo.utils.Streams;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

final class DevfileYamlParser {

  static Path findDevfile(final Path baseDirectory, final List<String> locations) {
    return Streams.findFirst(baseDirectory, locations).orElseThrow(DevfileYamlMissingException::new);
  }

  static DevfileYaml parseDevfile(final Path devfile) {
    try {
      final var mapper = new ObjectMapper(new YAMLFactory());
      mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      return mapper.readValue(devfile.toFile(), DevfileYaml.class);
    } catch (final JsonProcessingException exception) {
      throw new JsonParsingException(exception);
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  private DevfileYamlParser() {
    // utility class
  }


}
