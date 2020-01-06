/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.cli.commands;

import picocli.CommandLine;
import wtf.metio.ilo.cli.options.ShellOptions;
import wtf.metio.ilo.cli.spi.Exec;
import wtf.metio.ilo.cli.spi.Tools;
import wtf.metio.ilo.tools.api.CliTool;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

@CommandLine.Command(
    name = "shell",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true,
    showDefaultValues = true
)
public class OpenShell implements Callable<Integer> {

  @CommandLine.Mixin
  private ShellOptions options;

  public OpenShell() {
    // used by picoli
  }

  public OpenShell(final ShellOptions options) {
    this.options = options;
  }

  @Override
  public Integer call() {
    return Exec.executables()
        .flatMap(executables -> Tools.detectedTools(executables)
            .map(CliTool::name)
            .filter(options.runtime::matches)
            .map(this::toolOptions)
            .peek(this::debug)
            .map(executables::runAndAttach))
        .findFirst()
        .orElse(CommandLine.ExitCode.USAGE);
  }

  private List<String> toolOptions(final String tool) {
    final var currentDir = System.getProperty("user.dir");
    final var run = Stream.of(
        tool,
        "run",
        "--rm",
        "--interactive",
        "--tty",
        "--volume", currentDir + ":" + currentDir + ":Z",
        "--workdir", currentDir
    );
    final var extras = Stream.of(
        optional(options.name, "--name"),
        optional(options.hostname, "--hostname"),
        asStringWithPrefix(options.labels, "--label"),
        asStringWithPrefix(options.volumes, "--volume"))
        .flatMap(identity());
    final var command = Stream.of(
        options.image,
        String.join(" ", options.commands));
    return Stream.of(run, extras, command)
        .flatMap(identity())
        .collect(toList());
  }

  private void debug(final List<String> arguments) {
    if (options.debug) {
      System.out.println("ilo executes: " + String.join(" ", arguments));
    }
  }

  private Stream<String> optional(final String option, final String prefix) {
    return Stream.ofNullable(option)
        .filter(not(String::isBlank))
        .flatMap(value -> Stream.of(prefix, value));
  }

  private Stream<String> asStringWithPrefix(final List<String> values, final String prefix) {
    return Stream.ofNullable(values)
        .flatMap(List::stream)
        .filter(not(String::isBlank))
        .flatMap(value -> Stream.of(prefix, value));
  }

}
