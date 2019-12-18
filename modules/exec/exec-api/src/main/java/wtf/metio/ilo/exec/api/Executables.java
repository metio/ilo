package wtf.metio.ilo.exec.api;

import java.util.Optional;

public interface Executables {

  Optional<String> runAndReadOutput(String... args);

  int runAndAttach(String... args);

}
