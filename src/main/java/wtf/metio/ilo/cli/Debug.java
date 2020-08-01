/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import java.util.List;

// TODO: move class to 'cli' package
public final class Debug {

  // use commandLine logger from picocli
  public static void showExecutedCommand(final boolean debugEnabled, final List<String> arguments) {
    if (debugEnabled) {
      System.out.println("ilo executes: " + String.join(" ", arguments));
    }
  }

  private Debug() {
    // utility class
  }

}
