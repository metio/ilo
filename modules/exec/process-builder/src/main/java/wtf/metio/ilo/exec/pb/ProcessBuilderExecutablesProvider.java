package wtf.metio.ilo.exec.pb;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.exec.api.ExecutablesProvider;

import java.util.Optional;

public class ProcessBuilderExecutablesProvider implements ExecutablesProvider {

  @Override
  public Optional<Executables> get() {
    return Optional.of(new ProcessBuilderExecutables());
  }

}
