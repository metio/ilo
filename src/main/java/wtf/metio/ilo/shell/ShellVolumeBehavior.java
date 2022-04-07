/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.errors.LocalDirectoryDoesNotExistException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static wtf.metio.ilo.utils.Streams.filter;
import static wtf.metio.ilo.utils.Streams.fromList;

/**
 * Controls how the 'ilo shell' command handles missing local mount directories.
 */
public enum ShellVolumeBehavior {

  /**
   * Automatically create local volume mount directories that do not exist. If local directory cannot be created,
   * behave like WARN and remove from --volume list.
   */
  CREATE {
    @Override
    boolean handleMissingDirectory(final Path directory) {
      try {
        if (Files.notExists(directory)) {
          Files.createDirectories(directory);
        }
        return true;
      } catch (final IOException exception) {
        System.err.println("Could not create directory " + directory.toAbsolutePath() + " because of " + exception.getMessage());
        return false;
      }
    }
  },

  /**
   * Warn in case local mount directories do not exist remove them from --volume list.
   */
  WARN {
    @Override
    boolean handleMissingDirectory(final Path directory) {
      if (Files.exists(directory)) {
        return true;
      }
      System.out.println("The local directory " + directory.toAbsolutePath() + " does not exist.");
      return false;
    }
  },

  /**
   * Error in case local mount directories do not exist and stop execution.
   */
  ERROR {
    @Override
    boolean handleMissingDirectory(final Path directory) {
      if (Files.notExists(directory)) {
        throw new LocalDirectoryDoesNotExistException(directory);
      }
      return true;
    }
  };

  public List<String> handleLocalDirectories(final List<String> volumes) {
    return filter(fromList(volumes))
      .filter(this::handleLocalDirectory)
      .collect(toList());
  }

  private boolean handleLocalDirectory(final String volume) {
    final var localDirectory = extractLocalPart(volume);
    final var localPath = Paths.get(localDirectory);
    return handleMissingDirectory(localPath);
  }

  // visible for testing
  static String extractLocalPart(final String volume) {
    return volume.split(":")[0];
  }

  abstract boolean handleMissingDirectory(Path directory);

}
