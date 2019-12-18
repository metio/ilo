package wtf.metio.ilo.tools.kubectl;

import wtf.metio.ilo.exec.api.Executables;

import java.util.Optional;
import java.util.function.Function;

public interface KubectlProvider extends Function<Executables, Optional<KubectlCli>> {

  // marker interface

}
