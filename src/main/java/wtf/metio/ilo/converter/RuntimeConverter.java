/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.converter;

import picocli.CommandLine;
import wtf.metio.ilo.model.Runtime;

public final class RuntimeConverter implements CommandLine.ITypeConverter<Runtime> {

  @Override
  public Runtime convert(final String value) {
    return Runtime.fromAlias(value);
  }

}