module wtf.metio.ilo.tools.podman.jdk {

  requires wtf.metio.ilo.exec.api;
  requires wtf.metio.ilo.tools.podman;
  requires wtf.metio.ilo.os.generic;

  provides wtf.metio.ilo.tools.podman.PodmanProvider
      with wtf.metio.ilo.tools.podman.jdk.JdkPodmanProvider;

}
