package wtf.metio.ilo.cli.spi;

import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.exec.api.ExecutablesProvider;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public final class Exec {

  private Exec() {
    // utility class
  }

  public static Optional<Executables> executables() {
    return ServiceLoader.load(ExecutablesProvider.class).findFirst().flatMap(Supplier::get);
  }

}
