package wtf.metio.ilo.tools.kubectl.jdk;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.os.generic.ExecutablePaths;
import wtf.metio.ilo.tools.kubectl.KubectlCli;

import java.nio.file.Path;
import java.util.Optional;

public final class JdkKubectl implements KubectlCli {

  private final Executables executables;

  public JdkKubectl(final Executables executables) {
    this.executables = executables;
  }

  @Override
  public Optional<String> version() {
    return executables.runAndReadOutput(Constants.KUBECTL_COMMAND,
        "version",
        "--short=true",
        "--client=true")
        .map(output -> output.replace("Client Version: v", ""))
        .map(String::strip);
  }

  @Override
  public Optional<Path> path() {
    return ExecutablePaths.of(Constants.KUBECTL_COMMAND);
  }

}
