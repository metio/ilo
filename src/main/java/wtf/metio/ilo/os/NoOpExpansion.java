/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.os;

/**
 * Fallback implementation that does no parameter expansion.
 */
public class NoOpExpansion extends ParameterExpansion {

  @Override
  public String substituteCommands(final String value) {
    return value;
  }

  @Override
  public String expandParameters(final String value) {
    return value;
  }

}
