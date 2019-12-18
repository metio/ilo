package wtf.metio.ilo.formats.ilo;

import wtf.metio.ilo.formats.api.RuntimeConfig;
import wtf.metio.ilo.formats.api.RuntimeConfigProvider;

public class IloRuntimeConfigProvider implements RuntimeConfigProvider {

  @Override
  public RuntimeConfig get() {
    return new IloRuntimeConfig();
  }

}
