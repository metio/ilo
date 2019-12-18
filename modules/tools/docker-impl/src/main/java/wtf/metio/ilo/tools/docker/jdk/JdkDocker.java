package wtf.metio.ilo.tools.docker.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.docker.DockerCli;

import java.nio.file.Path;
import java.util.Optional;

public final class JdkDocker implements DockerCli {

    private final Executables executables;

    public JdkDocker(final Executables executables) {
        this.executables = executables;
    }

    @Override
    public Optional<String> version() {
        return executables.runAndReadOutput(Constants.DOCKER_COMMAND, Constants.VERSION_FLAG)
            .map(output -> output.replace("Docker version", ""))
            .map(output -> output.substring(0, output.indexOf(",")))
            .map(String::strip);
    }

    @Override
    public Optional<Path> path() {
        return ExecutablePaths.of(Constants.DOCKER_COMMAND);
    }

}
