/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
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
