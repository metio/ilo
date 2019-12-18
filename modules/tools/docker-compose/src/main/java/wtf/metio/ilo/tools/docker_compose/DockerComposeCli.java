package wtf.metio.ilo.tools.docker_compose;

import wtf.metio.ilo.tools.api.CliTool;

/**
 * Represents the docker-compose CLI and its commands.
 */
public interface DockerComposeCli extends CliTool {

    @Override
    default String name() {
        return "docker-compose";
    }

}
