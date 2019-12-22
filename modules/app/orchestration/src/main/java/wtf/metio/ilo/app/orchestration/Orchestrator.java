/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.app.orchestration;

import wtf.metio.ilo.tools.api.CliTool;

import java.util.List;

public interface Orchestrator {

  List<Command> determineCommands(String[] arguments, List<? extends CliTool> tools);

  int runCommand(Command command);

}
