module wtf.metio.ilo.tools.podman_compose.jdk {

  requires wtf.metio.ilo.exec.api;
  requires wtf.metio.ilo.tools.podman_compose;
  requires wtf.metio.ilo.os.generic;

  provides wtf.metio.ilo.tools.podman_compose.PodmanComposeProvider
      with wtf.metio.ilo.tools.podman_compose.jdk.JdkPodmanComposeProvider;

}
