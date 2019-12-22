/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.cli.constants;

public final class ExitCodes {

  public static final int NO_PROBLEMS = 0;
  public static final int NO_ORCHESTRATOR = 100;
  public static final int NO_TOOLS = 200;
  public static final int NO_RUNTIME = 300;
  public static final int CATASTROPHIC_FAILURE = 666;

  private ExitCodes() {
    // utility class
  }

}
