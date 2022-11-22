/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devfile;

import picocli.CommandLine;
import wtf.metio.ilo.compose.ComposeRuntime;
import wtf.metio.ilo.compose.ComposeRuntimeConverter;
import wtf.metio.ilo.model.Options;
import wtf.metio.ilo.shell.ShellRuntime;
import wtf.metio.ilo.shell.ShellRuntimeConverter;

import java.util.List;

public final class DevfileOptions implements Options {

  @CommandLine.Option(
      names = {"--runtime"},
      description = "Specify the shell runtime to use. If none is specified, use auto-selection.",
      converter = ShellRuntimeConverter.class
  )
  public ShellRuntime runtime;

  @CommandLine.Option(
      names = {"--runtime-option"},
      description = "Options for the selected runtime itself."
  )
  public List<String> runtimeOptions;

  @CommandLine.Option(
      names = {"--debug"},
      description = "Show additional debug information."
  )
  public boolean debug;

  @CommandLine.Option(
      names = {"--runtime-pull-option"},
      description = "Options for the pull command of the selected runtime."
  )
  public List<String> runtimePullOptions;

  @CommandLine.Option(
      names = {"--pull"},
      description = "Pull image before opening shell."
  )
  public boolean pull;

  @CommandLine.Option(
      names = {"--runtime-run-option"},
      description = "Options for the run command of the selected runtime."
  )
  public List<String> runtimeRunOptions;

  @CommandLine.Option(
      names = {"--runtime-build-option"},
      description = "Options for the build command of the selected runtime."
  )
  public List<String> runtimeBuildOptions;

  @CommandLine.Option(
      names = {"--remove-image"},
      description = "Remove image after closing the shell."
  )
  public boolean removeImage;

  @CommandLine.Option(
      names = {"--runtime-cleanup-option"},
      description = "Options for the cleanup command of the selected runtime."
  )
  public List<String> runtimeCleanupOptions;

  @CommandLine.Option(
      names = {"--component"},
      description = "Specify the component to use. If none is specified, use first supported component."
  )
  public String component;

  @CommandLine.Parameters(
    index = "0..*",
    description = "List of possible locations for a devfile.yaml file. First found will be used.",
    defaultValue = "devfile.yaml .devfile.yaml",
    split = " "
  )
  public List<String> locations;

  @Override
  public boolean debug() {
    return debug;
  }

}
