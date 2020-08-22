/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.compose;

import picocli.CommandLine;
import wtf.metio.ilo.model.Options;

public final class ComposeOptions implements Options {

  @CommandLine.Option(
      names = {"--runtime"},
      description = "Specify the runtime to use. If none is specified, use auto-selection.",
      converter = ComposeRuntimeConverter.class
  )
  public ComposeRuntime runtime;

  @CommandLine.Option(
      names = {"--interactive"},
      description = "Allocate a pseudo TTY or not. Not used by pods-compose.",
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
      names = {"--service"},
      description = "Specify the service to use. Used by docker-/podman-compose."
  )
  public String service;

  @CommandLine.Option(
      names = {"--file"},
      description = "Specify the docker-compose.yml/footloose.yaml file to use.",
      defaultValue = "docker-compose.yml"
  )
  public String file;

  @Override
  public boolean debug() {
    return debug;
  }

}
