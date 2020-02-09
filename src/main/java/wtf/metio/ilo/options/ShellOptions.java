/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.options;

import picocli.CommandLine;
import wtf.metio.ilo.converter.RuntimeConverter;
import wtf.metio.ilo.model.Runtime;

import java.util.List;

public class ShellOptions {

  @CommandLine.Option(
      names = {"--runtime"},
      description = "Specify the runtime to use",
      defaultValue = "podman",
      converter = RuntimeConverter.class
  )
  public Runtime runtime;

  @CommandLine.Option(
      names = {"--image"},
      description = "Specify the container image to run",
      defaultValue = "fedora:latest"
  )
  public String image;

  @CommandLine.Option(
      names = {"--hostname"},
      description = "Specify the hostname of the container"
  )
  public String hostname;

  @CommandLine.Option(
      names = {"--name"},
      description = "Specify the name of the container"
  )
  public String name;

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
      names = {"--label"},
      description = "Specify a label for the container."
  )
  public List<String> labels;

  @CommandLine.Option(
      names = {"--volume"},
      description = "Specify a volume for the container."
  )
  public List<String> volumes;

  @CommandLine.Option(
      names = {"--env"},
      description = "Specify a environment variables for the container."
  )
  public List<String> environmentVariables;

  @CommandLine.Parameters(
      defaultValue = "/bin/bash"
  )
  public List<String> commands;

}
