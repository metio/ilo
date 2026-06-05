/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

/**
 * Fallback implementation that does no expansion: the value is returned verbatim.
 */
public class NoOpExpansion extends ParameterExpansion {

  @Override
  String expand(final String value) {
    return value;
  }

}
