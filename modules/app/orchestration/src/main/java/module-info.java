module wtf.metio.ilo.app.orchestration {

  requires org.tinylog.api;

  requires wtf.metio.ilo.exec.api;
  requires wtf.metio.ilo.tools.api;
  requires wtf.metio.ilo.app.help;
  requires wtf.metio.ilo.app.user_input;

  exports wtf.metio.ilo.app.orchestration;

  provides wtf.metio.ilo.app.orchestration.OrchestratorProvider
      with wtf.metio.ilo.app.orchestration.DefaultOrchestratorProvider;

}
