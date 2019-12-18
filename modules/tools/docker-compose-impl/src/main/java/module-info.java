module wtf.metio.ilo.tools.docker_compose.jdk {

  requires wtf.metio.ilo.exec.api;
  requires wtf.metio.ilo.os.generic;
  requires wtf.metio.ilo.tools.docker_compose;

  provides wtf.metio.ilo.tools.docker_compose.DockerComposeProvider
      with wtf.metio.ilo.tools.docker_compose.jdk.JdkDockerComposeProvider;

}
