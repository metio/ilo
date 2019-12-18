package wtf.metio.ilo.tools.docker_compose;

import wtf.metio.ilo.exec.api.Executables;

import java.util.Optional;
import java.util.function.Function;

/**
 * Service provider for docker-compose CLI instances.
 */
public interface DockerComposeProvider extends Function<Executables, Optional<DockerComposeCli>> {

  // marker interface

}
