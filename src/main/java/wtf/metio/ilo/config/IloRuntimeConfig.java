/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class IloRuntimeConfig implements RuntimeConfig {

  private static final List<String> COMMANDS = List.of("shell", "compose");

  private static Stream<String> readFile(final Path path) {
    try {
      if (Files.exists(path)) {
        return Files.readAllLines(path).stream()
            .map(String::strip)
            .filter(Predicate.not(String::isBlank))
            .filter(IloRuntimeConfig::isAnySupportedCommand)
            .map(String::strip);
      }
      return Stream.empty();
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  private static boolean isAnySupportedCommand(final String line) {
    return COMMANDS.stream().anyMatch(line::startsWith);
  }

  private static String extractCommand(final String line) {
    return COMMANDS.stream()
        .filter(line::startsWith)
        .findFirst()
        .orElse("UNKNOWN");
  }

  private static String removeCommandPrefix(final String line) {
    return COMMANDS.stream()
        .filter(line::startsWith)
        .map(command -> line.substring(command.length() + 1))
        .findFirst()
        .orElse(line);
  }

  @Override
  public Map<String, List<String>> readConfig() {
    final var userDir = System.getProperty("user.dir");
    final var workingDir = Paths.get(userDir);

    final var nestedUserConfig = workingDir.resolve(".ilo/ilo.user.rc");
    final var userConfig = workingDir.resolve(".ilo.user.rc");
    final var nestedConfig = workingDir.resolve(".ilo/ilo.rc");
    final var config = workingDir.resolve(".ilo.rc");

    return Stream.of(nestedUserConfig, userConfig, nestedConfig, config)
        .flatMap(IloRuntimeConfig::readFile)
        .collect(groupingBy(IloRuntimeConfig::extractCommand, HashMap::new, mapping(IloRuntimeConfig::removeCommandPrefix, toList())));
  }

}
