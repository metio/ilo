/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
 * @see <a href="https://picocli.info/#AtFiles">Picocli Argument Files</a>
 */
public final class RunCommands {

  /**
   * Locate run commands on the host machine and prepares them for loading by picocli by prepending a '@' in front of
   * the path. This turns them into argument files which are natively supported by picocli.
   *
   * @param baseDirectory The base directory to use for relative paths.
   * @return Stream of run command paths, prepended with '@'.
   */
  public static Stream<String> locate(final Path baseDirectory) {
    if (System.getenv().containsKey(EnvironmentVariables.ILO_RC.name())) {
      final var rcFiles = System.getenv().get(EnvironmentVariables.ILO_RC.name());
      final var files = rcFiles.split(",");
      return asArgumentFiles(Arrays.stream(files).map(String::trim).map(baseDirectory::resolve));
    }
    return asArgumentFiles(Stream.of(".ilo/ilo.rc", ".ilo.rc").map(baseDirectory::resolve));
  }

  private static Stream<String> asArgumentFiles(final Stream<? extends Path> locations) {
    return locations
        .filter(Files::isReadable)
        .filter(Files::isRegularFile)
        .map(Path::toAbsolutePath)
        .map(Path::toString)
        .map("@"::concat);
  }

  /**
   * Poor-mans guard to prohibit adding run command files in some 'special' cases, e.g. users wants to see 'help'.
   *
   * @param args The CLI arguments for ilo itself.
   * @return Whether to add run command files or not.
   */
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
    // utility class
  }

}
