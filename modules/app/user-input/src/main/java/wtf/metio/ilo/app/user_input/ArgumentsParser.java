/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.app.user_input;

import joptsimple.OptionParser;
import joptsimple.OptionSpec;

public final class ArgumentsParser {

  private static final OptionParser OPTION_PARSER = new OptionParser();
  private static final OptionSpec<Boolean> HELP_FLAG = OPTION_PARSER.accepts(UserArguments.HELP.name)
      .withOptionalArg()
      .ofType(Boolean.class)
      .defaultsTo(UserArguments.HELP.defaultValue);
  private static final OptionSpec<Boolean> DEBUG_FLAG = OPTION_PARSER.accepts(UserArguments.DEBUG.name)
      .withOptionalArg()
      .ofType(Boolean.class)
      .defaultsTo(UserArguments.DEBUG.defaultValue);
  private static final OptionSpec<String> RUNTIME_FLAG = OPTION_PARSER.accepts(UserArguments.RUNTIME.name)
      .withOptionalArg()
      .ofType(String.class)
      .defaultsTo(UserArguments.RUNTIME.defaultValue);
  private static final OptionSpec<String> IMAGE_FLAG = OPTION_PARSER.accepts(UserArguments.IMAGE.name)
      .withOptionalArg()
      .ofType(String.class)
      .defaultsTo(UserArguments.IMAGE.defaultValue);
  private static final OptionSpec<String> COMMAND_FLAG = OPTION_PARSER.accepts(UserArguments.COMMAND.name)
      .withOptionalArg()
      .ofType(String.class)
      .defaultsTo(UserArguments.COMMAND.defaultValue);

  private ArgumentsParser() {
    // utility class
  }

  public static Arguments parse(final String[] arguments) {
    OPTION_PARSER.allowsUnrecognizedOptions();
    final var options = OPTION_PARSER.parse(arguments);
    final var showHelp = options.has(HELP_FLAG);
    final var debug = options.has(DEBUG_FLAG);
    final var runtime = RUNTIME_FLAG.values(options);
    final var image = IMAGE_FLAG.values(options);
    final var command = COMMAND_FLAG.values(options);
    final var args = new Arguments();
    args.showHelp = showHelp;
    args.debug = debug;
    args.runtime = runtime.stream().findFirst().orElse(UserArguments.RUNTIME.defaultValue);
    args.image = image.stream().findFirst().orElse(UserArguments.IMAGE.defaultValue);
    args.command = command.stream().findFirst().orElse(UserArguments.COMMAND.defaultValue);
    return args;
  }

}
