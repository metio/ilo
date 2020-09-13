/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import picocli.CommandLine;
import wtf.metio.ilo.model.Options;

import java.util.List;
import java.util.Stack;

public final class ShellOptions implements Options {

  @CommandLine.Option(
      names = {"--runtime"},
      description = "Specify the runtime to use. If none is specified, use auto-selection.",
      converter = ShellRuntimeConverter.class
  )
  public ShellRuntime runtime;

  @CommandLine.Option(
      names = {"--debug"},
      description = "Show additional debug information."
  )
  public boolean debug;

  @CommandLine.Option(
      names = {"--pull"},
      description = "Pull image before opening shell."
  )
  public boolean pull;

  @CommandLine.Option(
      names = {"--no-interactive"},
      description = "Open interactive shell or just run a single command.",
      defaultValue = "true",
      negatable = true
  )
  public boolean interactive;

  @CommandLine.Option(
      names = {"--no-mount-project-dir"},
      description = "Mount the project directory into the running container.",
      defaultValue = "true",
      negatable = true
  )
  public boolean mountProjectDir;

  @CommandLine.Option(
      names = {"--dockerfile"},
      description = "The Dockerfile to use."
  )
  public String dockerfile;

  @CommandLine.Option(
      names = {"--context"},
      description = "The context to use when building an image.",
      defaultValue = "."
  )
  public String context;

  @CommandLine.Option(
      names = {"--remove-image"},
      description = "Remove image after closing the shell."
  )
  public boolean removeImage;

  @CommandLine.Option(
      names = {"--run-as"},
      description = "Run the container as the specified user:group."
  )
  public String runAs;

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

  @CommandLine.Option(
      names = {"--volume"},
      description = "Mount a volume into the container."
  )
  public List<String> volumes;

  @CommandLine.Option(
      names = {"--env"},
      description = "Specify a environment variable for the container."
  )
  public List<String> variables;

  @CommandLine.Option(
      names = {"--publish"},
      description = "Publish container ports to the host system."
  )
  public List<String> ports;

  @CommandLine.Parameters(
      index = "0",
      description = "The OCI image to use. In case --dockerfile is given as well, this defines the name of the resulting image.",
      defaultValue = "fedora:latest"
  )
  public String image;

  @CommandLine.Parameters(
      index = "1..*",
      description = "Command and its option(s) to run inside the container. Overwrites the command specified in the image."
  )
  public List<String> commands;

  @Override
  public boolean debug() {
    return debug;
  }

}
