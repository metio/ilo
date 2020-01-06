/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.cli.options;

import picocli.CommandLine;
import wtf.metio.ilo.cli.converter.RuntimesConverter;
import wtf.metio.ilo.cli.model.Runtimes;

import java.util.List;

public class ShellOptions {

  @CommandLine.Option(
      names = {"--runtime", "-r"},
      description = "Specify the runtime to use.",
      defaultValue = "podman",
      converter = RuntimesConverter.class
  )
  public Runtimes runtime;

  @CommandLine.Option(
      names = {"--image", "-i"},
      description = "Specify the container image to run.",
      defaultValue = "fedora:latest"
  )
  public String image;

  @CommandLine.Option(
      names = {"--hostname"},
      description = "Specify the hostname of the container."
  )
  public String hostname;

  @CommandLine.Option(
      names = {"--name"},
      description = "Specify the name of the container."
  )
  public String name;

  @CommandLine.Option(
      names = {"--debug"},
      description = "Show additional debug information"
  )
  public boolean debug;

  @CommandLine.Option(
      names = {"--label", "-l"},
      description = "Specify a label for the container."
  )
  public List<String> labels;

  @CommandLine.Option(
      names = {"--volume", "-v"},
      description = "Specify a volume for the container."
  )
  public List<String> volumes;

  @CommandLine.Parameters(
      defaultValue = "/bin/bash",
      paramLabel = "COMMAND"
  )
  public List<String> commands;

}
