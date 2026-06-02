/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Support for so-called RC files.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Run_commands">Run Commands</a>
 * @see <a href="https://picocli.info/#AtFiles">Picocli Argument Files</a>
 */
public final class RunCommands {

  /**
   * The flag that disables loading of run command files.
   *
   * <p>Run command files are resolved into picocli argument files <em>before</em> picocli parses, so
   * the decision cannot be read from the parsed command model. It is therefore matched against the
   * raw arguments in {@link #shouldAddRunCommands(String[])}. The flag is also declared as an option
   * on the {@code Ilo} command (using this same constant) so picocli accepts and documents it; that
   * option carries no behavior of its own. Both sites share this constant so the flag name cannot
   * drift between where it is declared and where it is acted upon.</p>
   */
  public static final String NO_RC_FLAG = "--no-rc";

  /**
   * Locate run commands on the host machine and prepares them for loading by picocli by prepending a '@' in front of
   * the path. This turns them into argument files which are natively supported by picocli.
   *
   * @param baseDirectory The base directory to use for relative paths.
   * @return Stream of run command paths, prepended with '@'.
   */
  public static Stream<String> locate(final Path baseDirectory) {
    return locate(baseDirectory, new RcTrustGate());
  }

  // visible for testing
  static Stream<String> locate(final Path baseDirectory, final Predicate<? super Path> trustGate) {
    if (System.getenv().containsKey(EnvironmentVariables.ILO_RC.name())) {
      // Files named explicitly via ILO_RC are an opt-in by the user and are loaded as-is.
      final var rcFiles = System.getenv().get(EnvironmentVariables.ILO_RC.name());
      final var files = rcFiles.split(",");
      return asArgumentFiles(readable(Arrays.stream(files).map(String::trim).map(baseDirectory::resolve)));
    }
    // Files discovered implicitly in the working directory are untrusted input: a run command file
    // can run arbitrary commands on the host when ilo expands option values, so it is only loaded
    // once the trust gate has confirmed it.
    return asArgumentFiles(readable(Stream.of(".ilo/ilo.rc", ".ilo.rc").map(baseDirectory::resolve))
        .filter(trustGate));
  }

  private static Stream<Path> readable(final Stream<? extends Path> locations) {
    return locations
        .filter(Files::isReadable)
        .filter(Files::isRegularFile)
        .map(Path::toAbsolutePath);
  }

  private static Stream<String> asArgumentFiles(final Stream<? extends Path> locations) {
    return locations
        .map(Path::toString)
        .map("@"::concat);
  }

  /**
   * Poor-mans guard to prohibit adding run command files in some 'special' cases, e.g. users wants to see 'help'
   * or has disabled run commands with {@value #NO_RC_FLAG}.
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
    final var disableRunCommands = hasArguments && NO_RC_FLAG.equals(args[0]);

    return !isVersion && !isHelp && !isCompletion && !disableRunCommands;
  }

  private RunCommands() {
    // utility class
  }

}
