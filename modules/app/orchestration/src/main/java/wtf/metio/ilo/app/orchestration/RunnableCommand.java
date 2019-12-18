package wtf.metio.ilo.app.orchestration;

import org.tinylog.Logger;
import wtf.metio.ilo.exec.api.Executables;

public class RunnableCommand implements Command {

  private final Runnable runnable;
  private final int failureCode;

  public RunnableCommand(final Runnable runnable, final int failureCode) {
    this.runnable = runnable;
    this.failureCode = failureCode;
  }

  @Override
  public int run(final Executables executables) {
    try {
      runnable.run();
      return 0;
    } catch (final Exception exception) {
      Logger.error(exception);
      return failureCode;
    }
  }

}
