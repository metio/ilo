/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.utils;

import wtf.metio.ilo.options.ComposeOptions;
import wtf.metio.ilo.options.ShellOptions;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

public final class CalculateArguments {

  private CalculateArguments() {
    // utility class
  }

  public static List<String> shellArguments(final ShellOptions options, final String tool) {
    final var currentDir = System.getProperty("user.dir");
    final var run = Stream.of(
        tool,
        "run",
        "--rm",
        "--volume", currentDir + ":" + currentDir + ":Z",
        "--workdir", currentDir
    );
    final var tty = options.interactive ? Stream.of(
        "--interactive",
        "--tty"
    ) : Stream.<String>empty();
    final var extras = Stream.of(
        optional(options.name, "--name"),
        optional(options.hostname, "--hostname"),
        asStringWithPrefix(options.labels, "--label"),
        asStringWithPrefix(expandHomeDirectory(options.volumes), "--volume"))
        .flatMap(identity());
    final var command = Stream.concat(
        Stream.of(options.image),
        options.commands.stream());
    return Stream.of(run, tty, extras, command)
        .flatMap(identity())
        .collect(toList());
  }

  public static List<String> composeRunArguments(final ComposeOptions options, final String tool) {
    final var run = Stream.of(
        tool,
        "--file", options.composeFile,
        "run",
        "--rm"
    );
    final var tty = options.interactive ? Stream.<String>empty() : Stream.of("-T");
    final var service = Stream.of(options.service);
    return Stream.of(run, tty, service)
        .flatMap(identity())
        .collect(toList());
  }

  public static List<String> composeCleanupArguments(final ComposeOptions options, final String tool) {
    return List.of(
        tool,
        "--file", options.composeFile,
        "down"
    );
  }

  private static Stream<String> optional(final String option, final String prefix) {
    return Stream.ofNullable(option)
        .filter(not(String::isBlank))
        .flatMap(value -> Stream.of(prefix, value));
  }

  private static Stream<String> asStringWithPrefix(final List<String> values, final String prefix) {
    return Stream.ofNullable(values)
        .flatMap(List::stream)
        .filter(not(String::isBlank))
        .flatMap(value -> Stream.of(prefix, value));
  }

  private static List<String> expandHomeDirectory(final List<String> values) {
    final var userHome = System.getProperty("user.home");
    return Stream.ofNullable(values)
        .flatMap(List::stream)
        .map(value -> value.replace("$HOME", userHome))
        .map(value -> value.replace("~", userHome))
        .collect(Collectors.toList());
  }

}
