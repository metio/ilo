package wtf.metio.ilo.tools.podman;

import wtf.metio.ilo.tools.api.CliTool;

/**
 * Represents the podman CLI and its commands.
 */
public interface PodmanCli extends CliTool {

  @Override
  default String name() {
    return "podman";
  }

}
