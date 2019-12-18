package wtf.metio.ilo.tools.podman_compose;

import wtf.metio.ilo.exec.api.Executables;

import java.util.Optional;
import java.util.function.Function;

/**
 * Service provider for podman-compose CLI instances.
 */
public interface PodmanComposeProvider extends Function<Executables, Optional<PodmanComposeCli>> {

  // marker interface

}
