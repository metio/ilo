module wtf.metio.ilo.cli {

  requires org.tinylog.api;

  requires wtf.metio.ilo.exec.api;
  requires wtf.metio.ilo.formats.api;
  requires wtf.metio.ilo.tools.api;
  requires wtf.metio.ilo.tools.podman;
  requires wtf.metio.ilo.tools.podman_compose;
  requires wtf.metio.ilo.tools.buildah;
  requires wtf.metio.ilo.tools.docker;
  requires wtf.metio.ilo.tools.docker_compose;
  requires wtf.metio.ilo.tools.kubectl;
  requires wtf.metio.ilo.app.orchestration;
  requires wtf.metio.ilo.app.help;

  uses wtf.metio.ilo.tools.podman.PodmanProvider;
  uses wtf.metio.ilo.tools.buildah.BuildahProvider;
  uses wtf.metio.ilo.tools.kubectl.KubectlProvider;
  uses wtf.metio.ilo.tools.docker.DockerProvider;
  uses wtf.metio.ilo.tools.docker_compose.DockerComposeProvider;
  uses wtf.metio.ilo.exec.api.ExecutablesProvider;
  uses wtf.metio.ilo.app.orchestration.OrchestratorProvider;
  uses wtf.metio.ilo.tools.podman_compose.PodmanComposeProvider;
  uses wtf.metio.ilo.formats.api.RuntimeConfigProvider;

}
