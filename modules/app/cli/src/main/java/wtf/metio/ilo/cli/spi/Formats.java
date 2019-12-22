/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

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
