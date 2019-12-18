package wtf.metio.ilo.app.orchestration;

import wtf.metio.ilo.tools.api.CliTool;

import java.util.List;

public interface Orchestrator {

  List<Command> determineCommands(String[] arguments, List<? extends CliTool> tools);

  int runCommand(Command command);

}
