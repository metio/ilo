/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
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
  public int runAndWaitForExit(final List<String> args) {
    try {
      return new ProcessBuilder(args).inheritIO().start().waitFor();
    } catch (final IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
