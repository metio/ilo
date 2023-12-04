/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
  ILO_RC,

  /**
   * The runtime to use for 'ilo shell'. Can be overwritten with the '--runtime' flag.
   *
   * @see wtf.metio.ilo.shell.ShellOptions#runtime
   */
  ILO_SHELL_RUNTIME,

}
