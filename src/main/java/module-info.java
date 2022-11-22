module wtf.metio.ilo {

  requires info.picocli;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.yaml;

  opens wtf.metio.ilo to info.picocli;
  opens wtf.metio.ilo.compose to info.picocli;
  opens wtf.metio.ilo.devcontainer to info.picocli, com.fasterxml.jackson.databind;
  opens wtf.metio.ilo.devfile to info.picocli, com.fasterxml.jackson.databind;
  opens wtf.metio.ilo.shell to info.picocli;
  opens wtf.metio.ilo.version to info.picocli;

}
