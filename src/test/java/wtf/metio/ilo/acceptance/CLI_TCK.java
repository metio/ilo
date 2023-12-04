/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.acceptance;

import org.junit.jupiter.api.BeforeEach;
import picocli.CommandLine;
import wtf.metio.ilo.Ilo;
import wtf.metio.ilo.shell.ShellCommand;
import wtf.metio.ilo.test.TestMethodSources;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class CLI_TCK extends TestMethodSources {

  protected CommandLine cmd;
  protected StringWriter output;

  @BeforeEach
  final void initializeCLI() {
    cmd = Ilo.commandLine();
    output = new StringWriter();
    cmd.setOut(new PrintWriter(output));
  }

  protected final ShellCommand parseShellCommand(final String... args) {
    final var parseResult = cmd.parseArgs(args);
    final var subcommand = parseResult.subcommand();
    return (ShellCommand) subcommand.commandSpec().userObject();
  }

}
