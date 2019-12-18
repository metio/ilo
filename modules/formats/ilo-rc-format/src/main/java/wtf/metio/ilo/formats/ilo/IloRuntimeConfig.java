package wtf.metio.ilo.formats.ilo;

import wtf.metio.ilo.formats.api.RuntimeConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class IloRuntimeConfig implements RuntimeConfig {

  private static String[] readFile(final Path path) {
    try {
      if (Files.exists(path)) {
        return Files.readAllLines(path).stream()
            .map(String::strip)
            .filter(Predicate.not(String::isBlank))
            .flatMap(line -> Arrays.stream(line.split("\\s+")))
            .map(String::strip)
            .toArray(String[]::new);
      }
      return new String[0];
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String[] readConfig() {
    final var userDir = System.getProperty("user.dir");
    final var workingDir = Paths.get(userDir);

    final var nestedUserConfig = workingDir.resolve(".ilo/ilo.user.rc");
    final var userConfig = workingDir.resolve(".ilo.user.rc");
    final var nestedConfig = workingDir.resolve(".ilo/ilo.rc");
    final var config = workingDir.resolve(".ilo.rc");

    return Stream.of(nestedUserConfig, userConfig, nestedConfig, config)
        .map(IloRuntimeConfig::readFile)
        .flatMap(Stream::of)
        .toArray(String[]::new);
  }

}
