package wtf.metio.ilo.app.orchestration;

import wtf.metio.ilo.exec.api.Executables;

import java.util.Optional;

public final class DefaultOrchestratorProvider implements OrchestratorProvider {

  @Override
  public Optional<Orchestrator> apply(final Executables executables) {
    return Optional.of(new DefaultOrchestrator(executables));
  }

}
