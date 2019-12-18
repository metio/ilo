package wtf.metio.ilo.exec.pb;

import wtf.metio.ilo.exec.api.Executables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

final class ProcessBuilderExecutables implements Executables {

  @Override
  public Optional<String> runAndReadOutput(final String[] args) {
    try {
      final var processBuilder = new ProcessBuilder(args);
      final var process = processBuilder.start();
      try (final var isr = new InputStreamReader(process.getInputStream());
           final var br = new BufferedReader(isr)) {
        final var builder = new StringBuilder();
        String line;
        while (null != (line = br.readLine())) {
          builder.append(line);
          builder.append(System.lineSeparator());
        }
        return Optional.of(builder.toString());
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int runAndAttach(final String... args) {
    try {
      return new ProcessBuilder(args).inheritIO().start().waitFor();
    } catch (final IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
