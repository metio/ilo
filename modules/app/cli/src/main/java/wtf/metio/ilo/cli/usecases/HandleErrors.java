/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.cli.usecases;

import java.util.List;

public final class HandleErrors {

  private HandleErrors() {
    // utility class
  }

  public static void handleMissingTools() {
    System.err.println("Could not detect any tools!");
    System.err.println();
    System.err.println("Make sure that at least 'podman' is installed on your system.");
    System.err.println();
    System.err.println("See: https://ilo.metio.wtf/errors/missing-tool");
  }

  public static void handleMissingRuntime(final List<String> runtimes) {
    System.err.println("Could not detect any runtime!");
    System.err.println();
    System.err.println("ilo supports: " + String.join(" ", runtimes));
    System.err.println();
    System.err.println("Make sure that a matching runtime is installed on your system.");
    System.err.println();
    System.err.println("See: https://ilo.metio.wtf/errors/missing-runtime");
  }

}
