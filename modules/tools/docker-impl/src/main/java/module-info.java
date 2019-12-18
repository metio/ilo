module wtf.metio.ilo.tools.docker.jdk {

  requires wtf.metio.ilo.exec.api;
  requires wtf.metio.ilo.tools.docker;
  requires wtf.metio.ilo.os.generic;

  provides wtf.metio.ilo.tools.docker.DockerProvider
      with wtf.metio.ilo.tools.docker.jdk.JdkDockerProvider;

}
