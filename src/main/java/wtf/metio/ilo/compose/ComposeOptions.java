/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
      names = {"--interactive"},
      description = "Allocate a pseudo TTY or not.",
      defaultValue = "true",
      fallbackValue = "true",
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
      names = {"--fresh"},
      description = "Discard the reused services and start from a clean slate (recreate the containers)."
  )
  public boolean fresh;

  @CommandLine.Option(
      names = {"--keep-running"},
      description = "Leave the services running after you exit instead of stopping them."
  )
  public boolean keepRunning;

  @CommandLine.Option(
      names = {"--shell"},
      description = "The shell to run when attaching interactively without a command.",
      defaultValue = "/bin/sh"
  )
  public String shell;

  @CommandLine.Option(
      names = {"--file"},
      description = "Specify the docker-compose.yml/footloose.yaml file to use.",
      defaultValue = "docker-compose.yml"
  )
  public List<String> file;

  @CommandLine.Option(
      names = {"--runtime-option"},
      description = "Options for the selected compose runtime itself."
  )
  public List<String> runtimeOptions;

  @CommandLine.Option(
      names = {"--runtime-pull-option"},
      description = "Options for the pull command of the selected compose runtime."
  )
  public List<String> runtimePullOptions;

  @CommandLine.Option(
      names = {"--runtime-build-option"},
      description = "Options for the build command of the selected compose runtime."
  )
  public List<String> runtimeBuildOptions;

  @CommandLine.Option(
      names = {"--runtime-run-option"},
      description = "Options for the run command of the selected compose runtime."
  )
  public List<String> runtimeRunOptions;

  @CommandLine.Option(
      names = {"--runtime-cleanup-option"},
      description = "Options for the cleanup command of the selected compose runtime."
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
