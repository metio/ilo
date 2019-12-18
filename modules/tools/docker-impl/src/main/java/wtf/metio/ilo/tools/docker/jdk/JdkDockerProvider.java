package wtf.metio.ilo.tools.docker.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.docker.DockerCli;
import wtf.metio.ilo.tools.docker.DockerProvider;

import java.util.Optional;

public final class JdkDockerProvider implements DockerProvider {

  @Override
  public Optional<DockerCli> apply(final Executables executables) {
    return ExecutablePaths.of(Constants.DOCKER_COMMAND)
        .map(path -> new JdkDocker(executables));
  }

}
