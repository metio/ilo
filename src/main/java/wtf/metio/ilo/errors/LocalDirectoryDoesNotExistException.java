/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
