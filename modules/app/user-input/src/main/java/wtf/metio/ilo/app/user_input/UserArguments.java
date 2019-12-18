package wtf.metio.ilo.app.user_input;

import java.util.stream.Stream;

import static wtf.metio.ilo.app.user_input.UserArgument.arg;

public final class UserArguments {

  public static final UserArgument<Boolean> HELP = arg("help", Boolean.FALSE,
      "Show help information (you are reading them right now).");
  public static final UserArgument<Boolean> DEBUG = arg("debug", Boolean.FALSE,
      "Show extra debug information.");
  public static final UserArgument<String> RUNTIME = arg("runtime", "podman",
      "Picks the container runtime. Possible values are 'podman' and 'docker'.");
  public static final UserArgument<String> IMAGE = arg("image", "fedora:latest",
      "Selects the container image to run.");
  public static final UserArgument<String> COMMAND = arg("command", "/bin/bash",
      "Defines the command to run.");

  private UserArguments() {
    // utility class
  }

  public static Stream<UserArgument<?>> all() {
    return Stream.of(HELP, DEBUG, RUNTIME, IMAGE, COMMAND);
  }

}
