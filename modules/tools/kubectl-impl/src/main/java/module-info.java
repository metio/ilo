module wtf.metio.ilo.tools.kubectl.jdk {

  requires wtf.metio.ilo.exec.api;
  requires wtf.metio.ilo.tools.kubectl;
  requires wtf.metio.ilo.os.generic;

  provides wtf.metio.ilo.tools.kubectl.KubectlProvider
      with wtf.metio.ilo.tools.kubectl.jdk.JdkKubectlProvider;

}
