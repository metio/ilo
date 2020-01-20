/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.exec;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class ExecutablePaths {

  private ExecutablePaths() {
    // utility class
  }

  public static Optional<Path> of(final String exec) {
    return allPaths().map(path -> path.resolve(exec))
        .filter(ExecutablePaths::canExecute)
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

}
