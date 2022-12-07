/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import picocli.CommandLine;
import wtf.metio.ilo.model.CliTool;
import wtf.metio.ilo.model.Options;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * Utility class that encapsulates the common command lifecycle for 'ilo shell' and 'ilo compose'.
 */
public final class CommandLifecycle {

  /**
   * The common command lifecycle executes:
   * <ol>
   *   <li>Pull</li>
   *   <li>Build</li>
   *   <li>Run</li>
   *   <li>Cleanup</li>
   * </ol>>
   *
   * @param tool      The container tool to use, e.g. podman.
   * @param options   The options to use for the entire command lifecycle.
   * @param executor  The executor to use.
   * @param <OPTIONS> The type of the options supplied.
   * @param <CLI>     The type of the container tool supplied.
   * @return Stream of exit codes, one for each step in the lifecycle.
   */
  public static <OPTIONS extends Options, CLI extends CliTool<OPTIONS>> int run(
      final CLI tool,
      final OPTIONS options,
      final BiFunction<? super List<String>, ? super Boolean, Integer> executor) {
    final var pullArguments = tool.pullArguments(options);
    final var pullExitCode = executor.apply(pullArguments, options.debug());
    if (0 != pullExitCode) {
      return pullExitCode;
    }
    final var buildArguments = tool.buildArguments(options);
    final var buildExitCode = executor.apply(buildArguments, options.debug());
    if (0 != buildExitCode) {
      return buildExitCode;
    }
    final var runArguments = tool.runArguments(options);
    final var runExitCode = executor.apply(runArguments, options.debug());
    if (0 != runExitCode) {
      return runExitCode;
    }
    final var cleanupArguments = tool.cleanupArguments(options);
    final var cleanupExitCode = executor.apply(cleanupArguments, options.debug());
    return IntStream.of(pullExitCode, buildExitCode, runExitCode, cleanupExitCode)
        .max().orElse(CommandLine.ExitCode.SOFTWARE);
  }

  private CommandLifecycle() {
    // utility class
  }

}
