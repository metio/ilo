package wtf.metio.ilo.tools.docker_compose.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.docker_compose.DockerComposeCli;
import wtf.metio.ilo.tools.docker_compose.DockerComposeProvider;

import java.util.Optional;

public class JdkDockerComposeProvider implements DockerComposeProvider {

  @Override
  public Optional<DockerComposeCli> apply(final Executables executables) {
    return ExecutablePaths.of(Constants.DOCKER_COMPOSE_COMMAND)
        .map(path -> new JdkDockerComposeCli(executables));
  }

}
