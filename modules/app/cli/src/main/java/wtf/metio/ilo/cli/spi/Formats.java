package wtf.metio.ilo.cli.spi;

import wtf.metio.ilo.formats.api.RuntimeConfig;
import wtf.metio.ilo.formats.api.RuntimeConfigProvider;

import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class Formats {

  private Formats() {
    // utility class
  }

  public static String[] runtimeConfig(final String[] args) {
    return ServiceLoader.load(RuntimeConfigProvider.class)
        .findFirst()
        .map(Supplier::get)
        .map(RuntimeConfig::readConfig)
        .map(config -> Stream.of(args, config)
            .flatMap(Stream::of)
            .toArray(String[]::new))
        .orElse(args);
  }

}
