module wtf.metio.ilo.formats.ilo {

  requires wtf.metio.ilo.formats.api;

  provides wtf.metio.ilo.formats.api.RuntimeConfigProvider
      with wtf.metio.ilo.formats.ilo.IloRuntimeConfigProvider;

}
