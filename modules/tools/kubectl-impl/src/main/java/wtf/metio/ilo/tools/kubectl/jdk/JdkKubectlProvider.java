package wtf.metio.ilo.tools.kubectl.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.kubectl.KubectlCli;
import wtf.metio.ilo.tools.kubectl.KubectlProvider;

import java.util.Optional;

public class JdkKubectlProvider implements KubectlProvider {

  @Override
  public Optional<KubectlCli> apply(final Executables executables) {
    return ExecutablePaths.of(Constants.KUBECTL_COMMAND)
        .map(path -> new JdkKubectl(executables));
  }

}
