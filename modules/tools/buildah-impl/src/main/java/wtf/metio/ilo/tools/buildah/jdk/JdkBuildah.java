package wtf.metio.ilo.tools.buildah.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.buildah.BuildahCli;

import java.nio.file.Path;
import java.util.Optional;

public final class JdkBuildah implements BuildahCli {

  private final Executables executables;

  public JdkBuildah(final Executables executables) {
    this.executables = executables;
  }

  @Override
  public Optional<String> version() {
    return executables.runAndReadOutput(Constants.BUILDAH_COMMAND, Constants.VERSION_FLAG)
        .map(output -> output.replace("buildah version", ""))
        .map(output -> output.substring(0, output.indexOf("(") - 1))
        .map(String::strip);
  }

  @Override
  public Optional<Path> path() {
    return ExecutablePaths.of(Constants.BUILDAH_COMMAND);
  }

}
