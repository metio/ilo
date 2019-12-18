package wtf.metio.ilo.tools.podman_compose.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.podman_compose.PodmanComposeCli;
import wtf.metio.ilo.tools.podman_compose.PodmanComposeProvider;

import java.util.Optional;

public final class JdkPodmanComposeProvider implements PodmanComposeProvider {

  @Override
  public Optional<PodmanComposeCli> apply(final Executables executables) {
    return ExecutablePaths.of(Constants.PODMAN_COMPOSE_COMMAND)
        .map(path -> new JdkPodmanCompose(executables));
  }

}
