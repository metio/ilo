package wtf.metio.ilo.tools.docker;

import wtf.metio.ilo.exec.api.Executables;

import java.util.Optional;
import java.util.function.Function;

/**
 * Service provider for docker CLI instances.
 */
public interface DockerProvider extends Function<Executables, Optional<DockerCli>> {

  // marker interface

}
