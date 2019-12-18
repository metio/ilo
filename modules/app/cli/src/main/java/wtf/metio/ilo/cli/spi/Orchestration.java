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
