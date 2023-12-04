/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import picocli.CommandLine;
import wtf.metio.ilo.model.Options;

import java.util.List;

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
      names = {"--interactive"},
      description = "Open interactive shell or just run a single command.",
      defaultValue = "true",
      fallbackValue = "true",
      negatable = true
  )
  public boolean interactive;

  @CommandLine.Option(
      names = {"--mount-project-dir"},
      description = "Mount the project directory into the running container. Container path will be the same as --working-dir.",
      defaultValue = "true",
      fallbackValue = "true",
      negatable = true
  )
  public boolean mountProjectDir;

  @CommandLine.Option(
      names = {"--working-dir"},
      description = "The directory in the container to use. If not specified, defaults to the current directory."
  )
  public String workingDir;

  @CommandLine.Option(
      names = {"--containerfile", "--dockerfile"},
      description = "The Containerfile to use."
  )
  public String containerfile;

  @CommandLine.Option(
      names = {"--context"},
      description = "The context to use when building an image.",
      defaultValue = "."
  )
  public String context;

  @CommandLine.Option(
      names = {"--hostname"},
      description = "The hostname of the running container."
  )
  public String hostname;

  @CommandLine.Option(
      names = {"--remove-image"},
      description = "Remove image after closing the shell."
  )
  public boolean removeImage;

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
      names = {"--missing-volumes"},
      description = "Specifies how missing local volume directories should be handles. Valid values: ${COMPLETION-CANDIDATES}",
      defaultValue = "CREATE"
  )
  public ShellVolumeBehavior missingVolumes;

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
      description = "The OCI image to use. In case --containerfile or --dockerfile is given as well, this defines the name of the resulting image.",
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
