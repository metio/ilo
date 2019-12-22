/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.app.help;

import org.tinylog.Logger;
import wtf.metio.ilo.app.user_input.UserArguments;
import wtf.metio.ilo.tools.api.CliTool;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Prints the help information, including how to use 'ilo'.
 */
public final class PrintHelp {

  private PrintHelp() {
    // utility class
  }

  public static void help(final Stream<? extends CliTool> tools, final int exitCode, final boolean debug) {
    usage();
    if (debug) {
      Logger.info("");
      detectedTools(tools);
    }
    System.exit(exitCode);
  }

  private static void usage() {
    Logger.info("ilo - manage reproducible build environments \uD83D\uDC8B️");
    Logger.info("");
    Logger.info("Usage");
    Logger.info("  ilo [OPTIONS]");
    Logger.info("");
    Logger.info("Options");
    UserArguments.all().forEach(argument -> Logger.info("  {}", argument));
  }

  private static void detectedTools(final Stream<? extends CliTool> tools) {
    Logger.info("This following tools are supported/installed on this system:");
    tools.forEach(tool -> Logger.info("  {}: {} [{}]",
        tool.name(),
        tool.version().map(version -> "v" + version).orElse(""),
        tool.path().map(Path::toString).orElse("")));
  }

}
