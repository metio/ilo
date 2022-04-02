/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import wtf.metio.ilo.errors.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Executables {

  public static Optional<Path> of(final String tool) {
    return allPaths().map(path -> path.resolve(tool))
      .filter(Executables::canExecute)
      .findFirst();
  }

  static Stream<Path> allPaths() {
    return Stream.of(System.getenv("PATH")
      .split(Pattern.quote(File.pathSeparator)))
      .map(Paths::get);
  }

  static boolean canExecute(final Path binary) {
    return Files.exists(binary) && Files.isExecutable(binary);
  }

  public static int runAndWaitForExit(final List<String> arguments, final boolean debug) {
    if (null == arguments || arguments.isEmpty()) {
      return 0;
    }
    if (debug) {
      System.out.println("ilo executes: " + String.join(" ", arguments));
    }
    try {
      return new ProcessBuilder(arguments).inheritIO().start().waitFor();
    } catch (final InterruptedException exception) {
      throw new UnexpectedInterruptionException(exception);
    } catch (final UnsupportedOperationException exception) {
      throw new OperatingSystemNotSupportedException(exception);
    } catch (final NullPointerException exception) {
      throw new CommandListContainsNullException(exception, arguments);
    } catch (final IndexOutOfBoundsException exception) {
      throw new CommandListIsEmptyException(exception);
    } catch (final SecurityException exception) {
      throw new SecurityManagerDeniesAccessException(exception);
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  public static String runAndReadOutput(final String... arguments) {
    try {
      final var processBuilder = new ProcessBuilder(arguments);
      final var process = processBuilder.start();
      try (final var reader = new InputStreamReader(process.getInputStream());
           final var buffer = new BufferedReader(reader)) {
        final var builder = new StringBuilder();
        String line;
        while (null != (line = buffer.readLine())) {
          builder.append(line);
          builder.append(System.lineSeparator());
        }
        return builder.toString().strip();
      }
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  private Executables() {
    // utility class
  }

}
