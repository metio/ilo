/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.os;

/**
 * Support for Windows CMD
 */
final class Cmd implements ParameterExpansion {

  @Override
  public String substituteCommands(final String value) {
    // TODO: cmd impl
    return null;
  }

  @Override
  public String expandParameters(final String value) {
    // TODO: cmd impl
    return null;
  }

}
