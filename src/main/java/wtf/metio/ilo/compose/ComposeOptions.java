/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import picocli.CommandLine;
import wtf.metio.ilo.model.Options;

import java.util.List;

public final class ComposeOptions implements Options {

  @CommandLine.Option(
      names = {"--runtime"},
      description = "Specify the runtime to use. If none is specified, use auto-selection.",
      converter = ComposeRuntimeConverter.class
  )
  public ComposeRuntime runtime;

  @CommandLine.Option(
      names = {"--no-interactive"},
      description = "Allocate a pseudo TTY or not.",
      defaultValue = "true",
      negatable = true
  )
  public boolean interactive;

  @CommandLine.Option(
      names = {"--debug"},
      description = "Show additional debug information."
  )
  public boolean debug;

  @CommandLine.Option(
      names = {"--pull"},
      description = "Pull images before opening shell."
  )
  public boolean pull;

  @CommandLine.Option(
      names = {"--build"},
      description = "Build images before opening shell."
  )
  public boolean build;

  @CommandLine.Option(
      names = {"--file"},
      description = "Specify the docker-compose.yml/footloose.yaml file to use.",
      defaultValue = "docker-compose.yml"
  )
  public String file;

  @CommandLine.Option(
      names = {"--runtime-option"},
      description = "Options for the selected runtime itself."
  )
  public List<String> runtimeOptions;

  @CommandLine.Option(
      names = {"--runtime-pull-option"},
      description = "Options for the pull command of the selected runtime."
  )
  public List<String> runtimePullOptions;

  @CommandLine.Option(
      names = {"--runtime-build-option"},
      description = "Options for the build command of the selected runtime."
  )
  public List<String> runtimeBuildOptions;

  @CommandLine.Option(
      names = {"--runtime-run-option"},
      description = "Options for the run command of the selected runtime."
  )
  public List<String> runtimeRunOptions;

  @CommandLine.Option(
      names = {"--runtime-cleanup-option"},
      description = "Options for the cleanup command of the selected runtime."
  )
  public List<String> runtimeCleanupOptions;

  @CommandLine.Parameters(
      index = "0",
      description = "Specify the service to run.",
      defaultValue = "dev"
  )
  public String service;

  @CommandLine.Parameters(index = "1..*")
  public List<String> arguments;

  @Override
  public boolean debug() {
    return debug;
  }

}
