/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.cli.spi;

import wtf.metio.ilo.app.orchestration.Orchestrator;
import wtf.metio.ilo.app.orchestration.OrchestratorProvider;
import wtf.metio.ilo.exec.api.Executables;

import java.util.Optional;
import java.util.ServiceLoader;

public final class Orchestration {

  private Orchestration() {
    // utility class
  }

  public static Optional<Orchestrator> orchestrator(final Executables executables) {
    return ServiceLoader.load(OrchestratorProvider.class).findFirst().flatMap(provider -> provider.apply(executables));
  }

}
