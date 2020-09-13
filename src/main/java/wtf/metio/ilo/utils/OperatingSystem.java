/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.utils;

import wtf.metio.ilo.cli.Executables;
import wtf.metio.ilo.errors.RuntimeIOException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static wtf.metio.ilo.utils.Streams.filter;
import static wtf.metio.ilo.utils.Streams.fromList;

public final class OperatingSystem {

  private static final Pattern SCRIPT_PATTERN = Pattern.compile("\\$\\((?<script>.+)\\)");

  public static List<String> expandHomeDirectory(final List<String> values) {
    final var userHome = System.getProperty("user.home");
    return filter(fromList(values))
        .map(value -> value.replace("$HOME", userHome))
        .map(value -> value.replace("${HOME}", userHome))
        .map(value -> value.replace("~", userHome))
        .collect(toList());
  }

  public static String evaluateScripts(final String runAs) {
    return Stream.ofNullable(runAs)
        .map(value -> value.split(":"))
        .flatMap(Arrays::stream)
        .map(OperatingSystem::evaluateScript)
        .collect(Collectors.joining(":"));
  }

  private static String evaluateScript(final String value) {
    final var matcher = SCRIPT_PATTERN.matcher(value);
    if (matcher.find()) {
      final var group = matcher.group("script");
      final var arguments = group.split(" ");
      return Executables.runAndReadOutput(arguments);
    }
    return value;
  }

  public static String passwdFile(final String runAs) {
    try {
      final var username = System.getProperty("user.name");
      final var tempFile = Files.createTempFile("ilo", ".passwd");
      tempFile.toFile().deleteOnExit();
      final var content = String.format("%s:x:%s::/home/%s:/bin/bash", username, evaluateScripts(runAs), username);
      Files.writeString(tempFile, content);
      return String.format("%s:/etc/passwd", tempFile.toAbsolutePath().toString());
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  private OperatingSystem() {
    // utility class
  }

}
