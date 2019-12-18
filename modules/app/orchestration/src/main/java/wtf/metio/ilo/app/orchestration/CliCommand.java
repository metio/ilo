package wtf.metio.ilo.app.orchestration;

import org.tinylog.Logger;
import wtf.metio.ilo.exec.api.Executables;
import wtf.metio.ilo.tools.api.CliTool;

import java.util.Arrays;
import java.util.stream.Stream;

public class CliCommand implements Command {

  private final CliTool tool;
  private final boolean debug;
  private final String[] arguments;

  public CliCommand(final CliTool tool, final boolean debug, final String... arguments) {
    this.tool = tool;
    this.debug = debug;
    this.arguments = arguments;
  }

  @Override
  public int run(final Executables executables) {
    final var args = Stream.concat(
        Stream.of(tool.name()),
        Arrays.stream(arguments)).toArray(String[]::new);
    if (debug) {
      Logger.info("ilo executes: {}", String.join(" ", args));
    }
    return executables.runAndAttach(args);
  }

}
