/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.BeforeEach;
import picocli.CommandLine;
import wtf.metio.ilo.Ilo;
import wtf.metio.ilo.commands.Shell;

import java.io.PrintWriter;
import java.io.StringWriter;

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

}
