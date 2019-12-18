package wtf.metio.ilo.app.orchestration;

import wtf.metio.ilo.exec.api.Executables;

import java.util.Optional;
import java.util.function.Function;

public interface OrchestratorProvider extends Function<Executables, Optional<Orchestrator>> {

  // marker interface

}
