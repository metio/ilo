package wtf.metio.ilo.app.orchestration;

import wtf.metio.ilo.exec.api.Executables;

public interface Command {

  int run(Executables executables);

}
