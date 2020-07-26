/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.test;

import java.util.stream.Stream;

public abstract class TestSources {

  private static Stream<String> shellRuntimes() {
    return Stream.of("podman", "docker", "p", "d", "lxd", "l");
  }

  private static Stream<String> dockerLikeRuntimes() {
    return Stream.of("podman", "docker", "p", "d");
  }

  private static Stream<String> composeRuntimes() {
    return Stream.of(
        "podman-compose",
        "docker-compose",
        "pods-compose",
        "footloose",
        "vagrant",
        "pc",
        "dc",
        "fl",
        "v",
        "pods"
    );
  }

}
