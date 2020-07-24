/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.options;

import picocli.CommandLine;
import wtf.metio.ilo.converter.RuntimeConverter;
import wtf.metio.ilo.runtimes.Runtime;

import java.util.List;

public final class ShellOptions {

  @CommandLine.Option(
      names = {"--runtime"},
      description = "Specify the runtime to use. If none is specified, use auto-detection.",
      converter = RuntimeConverter.class
  )
  public Runtime runtime;

  @CommandLine.Option(
      names = {"--debug"},
      description = "Show additional debug information"
  )
  public boolean debug;

  @CommandLine.Option(
      names = {"--interactive"},
      description = "Open interactive shell or just run a single command",
      defaultValue = "true",
      negatable = true
  )
  public boolean interactive;

  @CommandLine.Option(
      names = {"--mount-project-dir"},
      description = "Mount the project directory into the running container",
      defaultValue = "true",
      negatable = true
  )
  public boolean mountProjectDir;

  @CommandLine.Parameters
  public List<String> commands;

}
