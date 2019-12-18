package wtf.metio.ilo.os.generic;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class ExecutablePaths {

  private ExecutablePaths() {
    // utility class
  }

  public static Optional<Path> of(final String exec) {
    return Stream.of(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
        .map(Paths::get)
        .map(path -> path.resolve(exec))
        .filter(binary -> Files.exists(binary) && Files.isExecutable(binary))
        .findFirst();
  }

}
