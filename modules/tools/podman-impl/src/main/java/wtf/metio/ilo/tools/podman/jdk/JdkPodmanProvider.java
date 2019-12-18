package wtf.metio.ilo.tools.podman.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.podman.PodmanCli;
import wtf.metio.ilo.tools.podman.PodmanProvider;

import java.util.Optional;

public final class JdkPodmanProvider implements PodmanProvider {

  @Override
  public Optional<PodmanCli> apply(final Executables executables) {
    return ExecutablePaths.of(Constants.PODMAN_COMMAND)
        .map(path -> new JdkPodman(executables));
  }

}
