/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

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
