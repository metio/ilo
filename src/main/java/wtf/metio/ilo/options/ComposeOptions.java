/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.options;

import picocli.CommandLine;
import wtf.metio.ilo.converter.ComposeRuntimeConverter;
import wtf.metio.ilo.model.ComposeRuntime;

public class ComposeOptions {

  @CommandLine.Option(
      names = {"--runtime"},
      description = "Specify the runtime to use",
      defaultValue = "podman-compose",
      converter = ComposeRuntimeConverter.class
  )
  public ComposeRuntime runtime;

  @CommandLine.Option(
      names = {"--debug"},
      description = "Show additional debug information"
  )
  public boolean debug;

  @CommandLine.Option(
      names = {"--service"},
      description = "Specify the service to use"
  )
  public String service;

  @CommandLine.Option(
      names = {"--file"},
      description = "Specify the compose file to use",
      defaultValue = "docker-compose.yml"
  )
  public String composeFile;

}
