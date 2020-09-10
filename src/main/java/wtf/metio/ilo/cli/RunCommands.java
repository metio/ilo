/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Support for so called RC files.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Run_commands">Run Commands</a>
 */
public final class RunCommands {

  public static Stream<String> locate(final Path baseDirectory) {
    return Stream.of(".ilo/ilo.rc", ".ilo.rc")
        .map(baseDirectory::resolve)
        .filter(Files::exists)
        .filter(Files::isRegularFile)
        .filter(Files::isReadable)
        .map(Path::toAbsolutePath)
        .map(Path::toString)
        .map("@"::concat);
  }

  public static boolean shouldAddRunCommands(final String[] args) {
    final var isVersion = (0 < args.length && ("-V".equals(args[0]) || "--version".equals(args[0])))
        || 1 < args.length && ("-V".equals(args[1]) || "--version".equals(args[1]));
    final var isHelp = (0 < args.length && ("-h".equals(args[0]) || "--help".equals(args[0])))
        || (1 < args.length && ("-h".equals(args[1]) || "--help".equals(args[1])));
    final var isCompletion = 0 < args.length && "generate-completion".equals(args[0]);
    return !isVersion && !isHelp && !isCompletion;
  }

  private RunCommands() {
    // factory class
  }

}
