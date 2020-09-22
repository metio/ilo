/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.os;

import wtf.metio.ilo.cli.Executables;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static wtf.metio.ilo.utils.Streams.filter;
import static wtf.metio.ilo.utils.Streams.fromList;

/**
 * Support for GNU Bash
 */
final class Bash implements ParameterExpansion {

  private static final Pattern BASH_COMMAND_NEW_STYLE = Pattern.compile("\\$\\((?<expression>.+)\\)");
  private static final Pattern BASH_COMMAND_OLD_STYLE = Pattern.compile("`(?<expression>.+)`");
  private static final String EXPRESSION = "expression";

  public static List<String> expand(final List<String> values) {
    return filter(fromList(values))
      .map(Bash::expand)
      .collect(toList());
  }

  public static String expand(final String value) {
    return Optional.ofNullable(value)
      .map(Bash::expandHomeDirectory)
      .map(Bash::bashExpansion)
      .orElse(value);
  }

  public static String expandHomeDirectory(final String value) {
    final var userHome = System.getProperty("user.home");
    return value.replace("$HOME", userHome)
      .replace("${HOME}", userHome)
      .replace("~", userHome);
  }

  public static String bashExpansion(final String value) {
    return Executables.of("bash")
      .map(Path::toAbsolutePath)
      .map(bash -> substituteCommands(bash, value))
      .orElse(value);
  }

  @Override
  public String substituteCommands(final String value) {
    return Executables.of("bash")
      .map(Path::toAbsolutePath)
      .map(bash -> substituteCommands(bash, value))
      .orElse(value);
  }

  // https://www.gnu.org/software/bash/manual/html_node/Command-Substitution.html
  private static String substituteCommands(final Path bash, final String value) {
    final var newStyle = BASH_COMMAND_NEW_STYLE.matcher(value);
    final var oldStyle = BASH_COMMAND_OLD_STYLE.matcher(value);
    var current = value;
    while (newStyle.find()) {
      current = substitute(bash, newStyle, current);
    }
    while (oldStyle.find()) {
      current = substitute(bash, oldStyle, current);
    }
    return current;
  }

  private static String substitute(final Path bash, final Matcher matcher, final String current) {
    final var command = matcher.group(EXPRESSION);
    final var start = matcher.start(EXPRESSION);
    final var end = matcher.end(EXPRESSION);
    final var replacement = Executables.runAndReadOutput(new String[]{bash.toString(), "-c", command});
    return new StringBuilder(current).replace(start, end, replacement).toString();
  }

  @Override
  // https://www.gnu.org/software/bash/manual/html_node/Shell-Parameter-Expansion.html
  public String expandParameters(final String value) {
    return null;
  }

}
