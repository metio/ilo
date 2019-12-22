/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.cli.usecases;

import org.tinylog.Logger;

import java.util.List;

import static wtf.metio.ilo.cli.constants.ExitCodes.*;

public final class HandleErrors {

  private HandleErrors() {
    // utility class
  }

  public static void handleMissingOrchestrator() {
    Logger.error("Could not create instance of 'wtf.metio.ilo.cli.orchestration.Orchestrator'!");
    Logger.error("");
    Logger.error("Make sure that the module path that contains an implementation of:");
    Logger.error("  - wtf.metio.ilo.cli.orchestration.OrchestratorProvider");
    Logger.error("");
    Logger.error("See: https://example.com/list-of-known-modules-with-provider");
    System.exit(NO_ORCHESTRATOR);
  }

  public static void handleMissingTools() {
    Logger.error("Could not detect any tools!");
    Logger.error("");
    Logger.error("Make sure that at least 'podman' is installed on your system.");
    Logger.error("");
    Logger.error("See: https://example.com/ilo-setup-guide");
    System.exit(NO_TOOLS);
  }

  public static void handleMissingRuntime(final List<String> runtimes) {
    Logger.error("Could not detect any runtime!");
    Logger.error("");
    Logger.error("ilo supports: [{}]", String.join(" ", runtimes));
    Logger.error("");
    Logger.error("Make sure that a matching runtime is installed on your system.");
    Logger.error("");
    Logger.error("See: https://example.com/ilo-missing-runtimes");
    System.exit(NO_RUNTIME);
  }

}
