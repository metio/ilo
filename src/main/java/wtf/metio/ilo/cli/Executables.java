/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

/**
 * Utility class that interacts with executables found on the host machine.
 */
public final class Executables {

  /**
   * Resolves a tool by its name from the current $PATH. To match shell behavior, the first match will be returned.
   * Thus make sure to order your $PATH so that your preferred location will be picked first.
   *
   * @param tool The name of the tool to look up.
   * @return The path to the tool or an empty optional.
   */
  public static Optional<Path> of(final String tool) {
    final var names = candidateNames(tool, isWindows(), executableExtensions());
    return allPaths()
        .flatMap(directory -> names.stream().map(directory::resolve))
        .filter(Executables::canExecute)
        .findFirst();
  }

  // visible for testing
  static boolean isWindows() {
    return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows");
  }

  /**
   * Computes the file names to probe for a tool. On Windows an executable is found under its base
   * name plus an extension from {@code PATHEXT} (e.g. {@code docker.exe}), so a bare name like
   * {@code docker} would never match. A name that already carries an extension is used verbatim.
   *
   * @param tool       The tool name as requested, e.g. 'docker'.
   * @param windows    Whether the host is running Windows.
   * @param extensions The executable extensions to append on Windows, e.g. '.EXE'.
   * @return The candidate file names to look up on $PATH, in preference order.
   */
  // visible for testing
  static List<String> candidateNames(final String tool, final boolean windows, final List<String> extensions) {
    if (windows && !hasExtension(tool)) {
      return extensions.stream()
          .map(extension -> tool + extension)
          .collect(toList());
    }
    return List.of(tool);
  }

  // visible for testing
  static boolean hasExtension(final String tool) {
    final var fileName = Paths.get(tool).getFileName().toString();
    return 0 < fileName.lastIndexOf('.');
  }

  // visible for testing
  static List<String> executableExtensions() {
    return parseExtensions(System.getenv("PATHEXT"));
  }

  // visible for testing
  static List<String> parseExtensions(final String pathext) {
    final var raw = null == pathext || pathext.isBlank() ? ".COM;.EXE;.BAT;.CMD" : pathext;
    return Arrays.stream(raw.split(";"))
        .map(String::trim)
        .filter(not(String::isBlank))
        .collect(toList());
  }

  // visible for testing
  static Stream<Path> allPaths() {
    return Stream.of(System.getenv("PATH")
            .split(Pattern.quote(File.pathSeparator)))
        .map(Paths::get);
  }

  // visible for testing
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
