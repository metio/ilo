/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.cli;

import org.tinylog.Logger;
import wtf.metio.ilo.app.orchestration.Orchestrator;
import wtf.metio.ilo.cli.spi.Exec;
import wtf.metio.ilo.cli.spi.Formats;
import wtf.metio.ilo.cli.spi.Orchestration;
import wtf.metio.ilo.cli.spi.Tools;
import wtf.metio.ilo.cli.usecases.HandleErrors;
import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.tools.api.CliTool;

import java.util.List;
import java.util.function.Consumer;

import static wtf.metio.ilo.cli.constants.ExitCodes.CATASTROPHIC_FAILURE;
import static wtf.metio.ilo.cli.constants.ExitCodes.NO_PROBLEMS;

/**
 * Main entry point for Ilo - the build environment manager
 */
public final class Ilo {

  public static void main(final String[] args) {
    try {
      Exec.executables().ifPresentOrElse(
          handleUserRequest(Formats.runtimeConfig(args)),
          HandleErrors::handleMissingOrchestrator);
    } catch (final Throwable throwable) {
      Logger.error(throwable);
      System.exit(CATASTROPHIC_FAILURE);
    }
  }

  private static Consumer<? super Executables> handleUserRequest(final String[] args) {
    return executables -> Orchestration.orchestrator(executables).ifPresentOrElse(
        runCommands(Tools.detectedTools(executables), args),
        HandleErrors::handleMissingOrchestrator);
  }

  private static Consumer<? super Orchestrator> runCommands(final List<? extends CliTool> tools, final String[] args) {
    return orchestrator -> orchestrator.determineCommands(args, tools).stream()
        .map(orchestrator::runCommand)
        .filter(exitCode -> 0 < exitCode)
        .findFirst().ifPresentOrElse(
            System::exit,
            () -> System.exit(NO_PROBLEMS));
  }

}
