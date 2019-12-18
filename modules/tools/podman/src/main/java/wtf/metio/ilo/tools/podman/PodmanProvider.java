package wtf.metio.ilo.tools.podman;

import wtf.metio.ilo.exec.api.Executables;

import java.util.Optional;
import java.util.function.Function;

/**
 * Service provider for podman CLI instances.
 */
public interface PodmanProvider extends Function<Executables, Optional<PodmanCli>> {

  // marker interface

}
