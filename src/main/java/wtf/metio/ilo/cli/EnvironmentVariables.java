/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

/**
 * Enumeration of all known/supported environment variables. Their name on the host machine must match their
 * {@code name()} representation, e.g. 'ILO_RC.name()' is just 'ILO_RC'.
 */
public enum EnvironmentVariables {

  /**
   * Allows to specify the run command files to load during startup.
   *
   * @see RunCommands#locate(java.nio.file.Path)
   */
  ILO_RC

}
