/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
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
