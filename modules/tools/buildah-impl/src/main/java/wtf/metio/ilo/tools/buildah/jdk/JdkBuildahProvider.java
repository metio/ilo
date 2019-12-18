package wtf.metio.ilo.tools.buildah.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.buildah.BuildahCli;
import wtf.metio.ilo.tools.buildah.BuildahProvider;

import java.util.Optional;

public class JdkBuildahProvider implements BuildahProvider {

  @Override
  public Optional<BuildahCli> apply(final Executables executables) {
    return ExecutablePaths.of(Constants.BUILDAH_COMMAND)
        .map(path -> new JdkBuildah(executables));
  }

}
