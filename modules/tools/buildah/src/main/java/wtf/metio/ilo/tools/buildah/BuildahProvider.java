package wtf.metio.ilo.tools.buildah;

import wtf.metio.ilo.exec.api.Executables;

import java.util.Optional;
import java.util.function.Function;

/**
 * Service provider for buildah CLI instances.
 */
public interface BuildahProvider extends Function<Executables, Optional<BuildahCli>> {

  // marker interface

}
