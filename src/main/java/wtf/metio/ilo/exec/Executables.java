/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.exec;

import java.util.List;
import java.util.Optional;

public interface Executables {

  Optional<String> runAndReadOutput(String... args);

  int runAndWaitForExit(List<String> args);

}