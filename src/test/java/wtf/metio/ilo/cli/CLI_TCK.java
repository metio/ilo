/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import picocli.CommandLine;
import wtf.metio.ilo.Ilo;
import wtf.metio.ilo.commands.Compose;
import wtf.metio.ilo.commands.Shell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

abstract class CLI_TCK {

  protected Ilo app;
  protected CommandLine cmd;
  protected StringWriter output;

  @BeforeEach
  final void initializeCLI() {
    app = new Ilo();
    cmd = new CommandLine(app);
    output = new StringWriter();
    cmd.setOut(new PrintWriter(output));
  }

  protected final Shell shell(final String... args) {
    final var parseResult = cmd.parseArgs(args);
    final var subcommand = parseResult.subcommand();
    return (Shell) subcommand.commandSpec().userObject();
  }

  protected final Compose compose(final String... args) {
    final var parseResult = cmd.parseArgs(args);
    final var subcommand = parseResult.subcommand();
    return (Compose) subcommand.commandSpec().userObject();
  }

  private static Executable contains(final List<String> arguments, final String cmd) {
    return () -> Assertions.assertTrue(arguments.contains(cmd), () -> cmd + " not found");
  }

  protected final void assertCommandLine(final List<String> cmd, final List<String> arguments) {
    Assertions.assertAll("command line",
        cmd.stream()
            .map(value -> contains(arguments, value))
            .collect(Collectors.toList())
    );
  }

}
