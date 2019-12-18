package wtf.metio.ilo.cli.constants;

public final class ExitCodes {

  public static final int NO_PROBLEMS = 0;
  public static final int NO_ORCHESTRATOR = 100;
  public static final int NO_TOOLS = 200;
  public static final int NO_RUNTIME = 300;
  public static final int CATASTROPHIC_FAILURE = 666;

  private ExitCodes() {
    // utility class
  }

}
