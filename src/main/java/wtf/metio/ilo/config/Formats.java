/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Formats {

  private Formats() {
    // utility class
  }

  public static Map<String, List<String>> runtimeConfig() {
    final var config = new IloRuntimeConfig();
    return Optional.ofNullable(config.readConfig()).orElseGet(Collections::emptyMap);
  }

}
