/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.errors;

import java.nio.file.Path;

/**
 * Signals that a local directory that was supposed to be mounted into a container does not exist.
 */
public final class LocalDirectoryDoesNotExistException extends BusinessException {

  public LocalDirectoryDoesNotExistException(final Path directory) {
    super(110, "The directory " + directory.toAbsolutePath() + " does not exist. Create it first before mounting it or set --missing-volumes to CREATE.");
  }

}
