package wtf.metio.ilo.tools.docker;

import wtf.metio.ilo.tools.api.CliTool;

/**
 * Represents the docker CLI and its commands.
 */
public interface DockerCli extends CliTool {

    @Override
    default String name() {
        return "docker";
    }

}
