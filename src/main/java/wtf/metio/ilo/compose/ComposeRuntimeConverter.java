/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.compose;

import picocli.CommandLine;

public final class ComposeRuntimeConverter implements CommandLine.ITypeConverter<ComposeRuntime> {

  @Override
  public ComposeRuntime convert(final String value) {
    return ComposeRuntime.fromAlias(value);
  }

}
