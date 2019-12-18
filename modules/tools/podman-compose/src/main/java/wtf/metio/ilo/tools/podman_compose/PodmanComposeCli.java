package wtf.metio.ilo.tools.podman_compose;

import wtf.metio.ilo.tools.api.CliTool;

/**
 * Represents the podman-compose CLI and its commands.
 */
public interface PodmanComposeCli extends CliTool {

  @Override
  default String name() {
    return "podman-compose";
  }

}
