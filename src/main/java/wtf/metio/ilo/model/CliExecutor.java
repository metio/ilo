/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.model;

import wtf.metio.ilo.cli.Executables;

import java.util.List;

public interface CliExecutor<RUNTIME extends Runtime<CLI>, CLI extends CliTool<OPTIONS>, OPTIONS extends Options> {

  CLI selectRuntime(RUNTIME runtime);

  default int execute(final List<String> arguments, final boolean debug) {
    return Executables.runAndWaitForExit(arguments, debug);
  }

}
