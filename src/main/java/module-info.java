module wtf.metio.ilo {

  requires info.picocli;
  requires info.picocli.codegen;

  opens wtf.metio.ilo to info.picocli;
  opens wtf.metio.ilo.compose to info.picocli;
  opens wtf.metio.ilo.devcontainer to info.picocli;
  opens wtf.metio.ilo.shell to info.picocli;
  opens wtf.metio.ilo.version to info.picocli;

}
