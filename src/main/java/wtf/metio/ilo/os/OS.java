/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.os;

import wtf.metio.ilo.errors.RuntimeIOException;
import wtf.metio.ilo.utils.Strings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static wtf.metio.ilo.utils.Streams.filter;
import static wtf.metio.ilo.utils.Streams.fromList;

public final class OS {

  public static List<String> expand(final List<String> values) {
    return filter(fromList(values))
      .map(OS::expand)
      .collect(toList());
  }

  public static String expand(final String value) {
    final var expansion = expansion();
    return Optional.ofNullable(value)
      .map(expansion::expandParameters)
      .map(expansion::substituteCommands)
      .orElse(value);
  }

  static ParameterExpansion expansion() {
    return detectExpansionForOS(System.getProperty("os.name"));
  }

  private static ParameterExpansion detectExpansionForOS(final String osName) {
    if (Strings.isNotBlank(osName)) {
      final var name = osName.toLowerCase(Locale.ENGLISH);
      if (name.contains("linux") || name.contains("mac")) {
        return new Bash();
      }
      if (name.contains("win")) {
        return new PowerShell();
      }
    }
    return new NoOpExpansion();
  }

  public static Path passwdFile(final String runAs) {
    try {
      final var username = System.getProperty("user.name");
      final var tempFile = Files.createTempFile("ilo", ".passwd");
      tempFile.toFile().deleteOnExit();
      final var content = String.format("%s:x:%s::/home/%s:/bin/bash", username, expand(runAs), username);
      Files.writeString(tempFile, content);
      return tempFile.toAbsolutePath();
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  private OS() {
    // utility class
  }

}
