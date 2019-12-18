package wtf.metio.ilo.tools.podman.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.podman.PodmanCli;

import java.nio.file.Path;
import java.util.Optional;

public final class JdkPodman implements PodmanCli {

  private final Executables executables;

  public JdkPodman(final Executables executables) {
    this.executables = executables;
  }

  @Override
  public Optional<String> version() {
    return executables.runAndReadOutput(Constants.PODMAN_COMMAND, Constants.VERSION_FLAG)
        .map(output -> output.replace("podman version", ""))
        .map(String::strip);
  }

  @Override
  public Optional<Path> path() {
    return ExecutablePaths.of(Constants.PODMAN_COMMAND);
  }

}
