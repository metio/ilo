/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import picocli.CommandLine;

import java.util.List;

public final class ShellOptions {

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
      negatable = true
  )
  public boolean interactive;

  @CommandLine.Option(
      names = {"--mount-project-dir"},
      description = "Mount the project directory into the running container.",
      defaultValue = "true",
      negatable = true
  )
  public boolean mountProjectDir;

  @CommandLine.Option(
      names = {"--image"},
      description = "The OCI image to use.",
      defaultValue = "fedora:latest"
  )
  public String image;

  @CommandLine.Option(
      names = {"--remove-image"},
      description = "Remove image after closing the shell."
  )
  public boolean removeImage;

  @CommandLine.Parameters
  public List<String> commands;

}
