package wtf.metio.ilo.tools.podman_compose.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.podman_compose.PodmanComposeCli;

import java.nio.file.Path;
import java.util.Optional;

public final class JdkPodmanCompose implements PodmanComposeCli {

  private final Executables executables;

  public JdkPodmanCompose(final Executables executables) {
    this.executables = executables;
  }

  @Override
  public Optional<String> version() {
    return executables.runAndReadOutput(Constants.PODMAN_COMPOSE_COMMAND, Constants.VERSION_FLAG)
        .map(output -> output.replace("podman-compose version", ""))
        .map(String::strip);
  }

  @Override
  public Optional<Path> path() {
    return ExecutablePaths.of(Constants.PODMAN_COMPOSE_COMMAND);
  }

}
