package wtf.metio.ilo.tools.api;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Most generic CLI tool.
 */
public interface CliTool {

  /**
   * @return The name of the CLI tool.
   */
  String name();

    /**
     * @return The version of the CLI tool.
     */
    Optional<String> version();

  /**
   * @return The absolute path to the CLI tool.
   */
  Optional<Path> path();

}
