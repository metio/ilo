module wtf.metio.ilo {

  requires info.picocli;

  opens wtf.metio.ilo to info.picocli;
  opens wtf.metio.ilo.commands to info.picocli;
  opens wtf.metio.ilo.converter to info.picocli;
  opens wtf.metio.ilo.runtimes to info.picocli;
  opens wtf.metio.ilo.options to info.picocli;
  opens wtf.metio.ilo.version to info.picocli;

}
