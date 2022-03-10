/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Support for so-called RC files.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Run_commands">Run Commands</a>
 */
public final class RunCommands {

  public static Stream<String> locate(final Path baseDirectory) {
    if (System.getenv().containsKey(EnvironmentVariables.ILO_RC.name())) {
      final var rcFiles = System.getenv().get(EnvironmentVariables.ILO_RC.name());
      final var files = rcFiles.split(",");
      return asArgumentFiles(Arrays.stream(files).map(String::trim).map(baseDirectory::resolve));
    }
    return asArgumentFiles(Stream.of(".ilo/ilo.rc", ".ilo.rc").map(baseDirectory::resolve));
  }

  public static Stream<String> asArgumentFiles(final Stream<? extends Path> locations) {
    return locations
      .filter(Files::isReadable)
      .filter(Files::isRegularFile)
      .map(Path::toAbsolutePath)
      .map(Path::toString)
      .map("@"::concat);
  }

  public static boolean shouldAddRunCommands(final String[] args) {
    final var hasArguments = 0 < args.length;
    final var isVersion = (hasArguments && ("-V".equals(args[0]) || "--version".equals(args[0])))
      || 1 < args.length && ("-V".equals(args[1]) || "--version".equals(args[1]));
    final var isHelp = (hasArguments && ("-h".equals(args[0]) || "--help".equals(args[0])))
      || (1 < args.length && ("-h".equals(args[1]) || "--help".equals(args[1])));
    final var isCompletion = hasArguments && "generate-completion".equals(args[0]);
    final var disableRunCommands = hasArguments && "--no-rc".equals(args[0]);

    return !isVersion && !isHelp && !isCompletion && !disableRunCommands;
  }

  private RunCommands() {
    // factory class
  }

}
