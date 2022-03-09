/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import wtf.metio.ilo.errors.DevcontainerJsonMissingException;
import wtf.metio.ilo.errors.JsonParsingException;
import wtf.metio.ilo.errors.RuntimeIOException;
import wtf.metio.ilo.utils.Streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

final class DevcontainerJsonParser {

  static Path findJson(final Path baseDirectory, final List<String> locations) {
    return Streams.fromList(locations)
      .map(baseDirectory::resolve)
      .filter(Files::isReadable)
      .filter(Files::isRegularFile)
      .map(Path::toAbsolutePath)
      .findFirst()
      .orElseThrow(DevcontainerJsonMissingException::new);
  }

  static DevcontainerJson parseJson(final Path devcontainer) {
    try {
      final var json = Files.readString(devcontainer);
      final var mapper = new ObjectMapper();
      mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      return mapper.readValue(json, DevcontainerJson.class);
    } catch (final JsonProcessingException exception) {
      throw new JsonParsingException(exception);
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  private DevcontainerJsonParser() {
    // utility class
  }

}
