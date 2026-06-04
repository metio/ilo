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
      description = "Remove the container and its image after closing the shell, instead of keeping them for reuse."
  )
  public boolean removeImage;

  @CommandLine.Option(
      names = {"--fresh"},
      description = "Discard any reused container and start from a clean slate (rebuild and recreate)."
  )
  public boolean fresh;

  @CommandLine.Option(
      names = {"--override-command"},
      negatable = true,
      defaultValue = "true",
      fallbackValue = "true",
      description = "Replace the image's entrypoint and command with a keepalive so the container stays running for reuse (default). Use --no-override-command to instead rely on the image's own long-running process — for images that already stay up."
  )
  public boolean overrideCommand = true;

  // Defaults to false on a directly-constructed instance so the compose and devcontainer commands,
  // which build ShellOptions programmatically, opt in deliberately; picocli sets it to true for
  // 'ilo shell' via the option default below.
  @CommandLine.Option(
      names = {"--update-remote-user-uid"},
      negatable = true,
      defaultValue = "true",
      fallbackValue = "true",
      description = "Align the container user's UID/GID with your host user so files created in the mounted project stay owned by you (default). Use --no-update-remote-user-uid to leave the container user as-is. See the File Ownership documentation."
  )
  public boolean updateRemoteUserUID;

  @CommandLine.Option(
      names = {"--remote-user"},
      description = "The container user to run as and align with your host user. When omitted, the image's configured user is used."
  )
  public String remoteUser;

  // Resolved from updateRemoteUserUID, remoteUser, and the selected runtime before the container is
  // created; not a command-line option. Defaults to NONE so a directly-constructed instance maps nothing.
  public RemoteUserMapping userMapping = RemoteUserMapping.NONE;

  @CommandLine.Option(
      names = {"--shell"},
      description = "The shell to run when attaching interactively without a command.",
      defaultValue = "/bin/sh"
  )
  public String shell;

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

  // Extra material that defines the environment but is not expressed as a container option above —
  // used by 'ilo devcontainer' to fold the devcontainer.json (including its lifecycle commands) into
  // the container's identity, so editing that file recreates the container. Not a command-line option.
  private String identitySource;

  public String identitySource() {
    return identitySource;
  }

  public void identitySource(final String identitySource) {
    this.identitySource = identitySource;
  }

  @Override
  public boolean debug() {
    return debug;
  }

}
