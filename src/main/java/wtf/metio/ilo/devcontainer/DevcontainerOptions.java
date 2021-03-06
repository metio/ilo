/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import picocli.CommandLine;
import wtf.metio.ilo.compose.ComposeRuntime;
import wtf.metio.ilo.compose.ComposeRuntimeConverter;
import wtf.metio.ilo.shell.ShellRuntime;
import wtf.metio.ilo.shell.ShellRuntimeConverter;

public final class DevcontainerOptions {

  @CommandLine.Option(
    names = {"--shell-runtime", "-S"},
    description = "Specify the shell runtime to use. If none is specified, use auto-selection.",
    converter = ShellRuntimeConverter.class
  )
  public ShellRuntime shellRuntime;

  @CommandLine.Option(
    names = {"--compose-runtime", "-C"},
    description = "Specify the compose runtime to use. If none is specified, use auto-selection.",
    converter = ComposeRuntimeConverter.class
  )
  public ComposeRuntime composeRuntime;

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
    names = {"--mount-project-dir"},
    description = "Mount the project directory into the running container.",
    defaultValue = "true",
    negatable = true
  )
  public boolean mountProjectDir;

  @CommandLine.Option(
    names = {"--remove-image"},
    description = "Remove image after closing the shell."
  )
  public boolean removeImage;

}
