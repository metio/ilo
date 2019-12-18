module wtf.metio.ilo.tools.buildah.jdk {

  requires wtf.metio.ilo.exec.api;
  requires wtf.metio.ilo.tools.buildah;
  requires wtf.metio.ilo.os.generic;

  provides wtf.metio.ilo.tools.buildah.BuildahProvider
      with wtf.metio.ilo.tools.buildah.jdk.JdkBuildahProvider;

}
