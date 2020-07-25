/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import wtf.metio.ilo.tools.CliTool;

import java.util.List;

public interface ShellCLI extends CliTool<ShellOptions> {

  List<String> buildArguments(ShellOptions options);

  List<String> pullArguments(ShellOptions options);

  List<String> cleanupArguments(ShellOptions options);

}
