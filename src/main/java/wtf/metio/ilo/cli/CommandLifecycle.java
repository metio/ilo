/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
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

public final class CommandLifecycle {

  public static <OPTIONS extends Options, CLI extends CliTool<OPTIONS>> int run(
      final CLI tool,
      final OPTIONS options,
      final BiFunction<? super List<String>, ? super Boolean, Integer> executor) {
    final var pullArguments = tool.pullArguments(options);
    final var pullExitCode = executor.apply(pullArguments, options.debug());
    final var buildArguments = tool.buildArguments(options);
    final var buildExitCode = executor.apply(buildArguments, options.debug());
    final var runArguments = tool.runArguments(options);
    final var runExitCode = executor.apply(runArguments, options.debug());
    final var cleanupArguments = tool.cleanupArguments(options);
    final var cleanupExitCode = executor.apply(cleanupArguments, options.debug());
    return IntStream.of(pullExitCode, buildExitCode, runExitCode, cleanupExitCode)
        .max().orElse(CommandLine.ExitCode.SOFTWARE);
  }

}
