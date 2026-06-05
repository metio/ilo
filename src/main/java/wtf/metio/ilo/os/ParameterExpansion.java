/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

/**
 * Expands an option value the way the host shell would. Implementations differ by shell;
 * {@link NoOpExpansion} is the fallback that performs no expansion.
 */
abstract class ParameterExpansion {

  /**
   * Expands command substitutions, parameter references and a leading tilde in a value.
   *
   * @param value The value to expand.
   * @return The expanded value.
   */
  abstract String expand(String value);

}
