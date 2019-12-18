import wtf.metio.ilo.exec.api.ExecutablesProvider;
import wtf.metio.ilo.exec.pb.ProcessBuilderExecutablesProvider;

module wtf.metio.ilo.exec.pb {

  requires wtf.metio.ilo.exec.api;

  provides ExecutablesProvider
      with ProcessBuilderExecutablesProvider;

}
