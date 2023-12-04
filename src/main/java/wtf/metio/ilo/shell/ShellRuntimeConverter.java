/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import picocli.CommandLine;

public final class ShellRuntimeConverter implements CommandLine.ITypeConverter<ShellRuntime> {

  @Override
  public ShellRuntime convert(final String value) {
    return ShellRuntime.fromAlias(value);
  }

}
