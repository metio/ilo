/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.app.orchestration;

import org.tinylog.Logger;
import wtf.metio.ilo.exec.api.Executables;

public class RunnableCommand implements Command {

  private final Runnable runnable;
  private final int failureCode;

  public RunnableCommand(final Runnable runnable, final int failureCode) {
    this.runnable = runnable;
    this.failureCode = failureCode;
  }

  @Override
  public int run(final Executables executables) {
    try {
      runnable.run();
      return 0;
    } catch (final Exception exception) {
      Logger.error(exception);
      return failureCode;
    }
  }

}
