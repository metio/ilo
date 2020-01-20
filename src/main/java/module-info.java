module wtf.metio.ilo {

  requires info.picocli;

  opens wtf.metio.ilo.commands to info.picocli;
  opens wtf.metio.ilo.converter to info.picocli;
  opens wtf.metio.ilo.model to info.picocli;
  opens wtf.metio.ilo.options to info.picocli;

}
