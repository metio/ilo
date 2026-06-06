/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.devfile;

import picocli.CommandLine;
import wtf.metio.ilo.devfile.DevfileYaml.Component;
import wtf.metio.ilo.devfile.DevfileYaml.Container;
import wtf.metio.ilo.devfile.DevfileYaml.Image;
import wtf.metio.ilo.shell.ShellCommand;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.ilo.utils.Strings;
import wtf.metio.ilo.version.VersionProvider;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.ToIntFunction;

import static wtf.metio.ilo.devfile.DevfileYamlParser.findDevfile;
import static wtf.metio.ilo.devfile.DevfileYamlParser.parseDevfile;

@CommandLine.Command(
    name = "devfile",
    description = "Open an (interactive) shell using devfile",
    versionProvider = VersionProvider.class,
    mixinStandardHelpOptions = true,
    showAtFileInUsageHelp = true,
    usageHelpAutoWidth = true,
    showDefaultValues = true,
    descriptionHeading = "%n",
    optionListHeading = "%n"
)
public final class DevfileCommand implements Callable<Integer> {

  @CommandLine.Mixin
  public DevfileOptions options;

  // Runs the shell options derived from the devfile; the default opens a real shell, tests substitute
  // a probe so the mapping can be asserted without starting a container.
  private final ToIntFunction<ShellOptions> shellRunner;

  // default constructor for picocli
  public DevfileCommand() {
    this(DevfileCommand::openShell);
  }

  // constructor for testing
  DevfileCommand(final ToIntFunction<ShellOptions> shellRunner) {
    this.shellRunner = shellRunner;
  }

  @Override
  public Integer call() {
    final var currentDir = Paths.get(System.getProperty("user.dir"));
    final var devfile = parseDevfile(findDevfile(currentDir, options.locations));

    if (hasSupportedDevfileConfiguration(devfile, options.component)) {
      return shellRunner.applyAsInt(mapOptions(options, devfile));
    }

    warnNoSupportedComponent(devfile, options.component);
    return CommandLine.ExitCode.USAGE;
  }

  // Explains the message-less failure: ilo can open a component that defines an image or builds from a
  // local Dockerfile (one with a 'uri'); a component with no container/image, or one whose Dockerfile is
  // sourced from git/a registry, is not something ilo can open. Lists the names present so a mistyped
  // '--component' is obvious.
  private static void warnNoSupportedComponent(final DevfileYaml devfile, final String component) {
    final var names = devfile.components().stream().map(Component::name).filter(Objects::nonNull).toList();
    final var what = Strings.isBlank(component)
        ? "no component ilo can open (one with a predefined image or a local Dockerfile)"
        : "no component named '" + component + "' that ilo can open";
    System.err.println("ilo: " + what + " was found in this devfile. Components present: " + names + ".");
  }

  private static int openShell(final ShellOptions options) {
    final var command = new ShellCommand();
    command.options = options;
    return command.call();
  }

  static boolean hasSupportedDevfileConfiguration(final DevfileYaml devfile, final String component) {
    return selectComponent(devfile, component).isPresent();
  }

  // Picks the component to run: the one named by '--component', or — when none is requested — the first
  // component ilo can handle, in document order. The name is matched on the requested value so a
  // component without a name simply never matches rather than throwing.
  private static Optional<Component> selectComponent(final DevfileYaml devfile, final String component) {
    return devfile.components().stream()
        .filter(c -> Strings.isBlank(component) || component.equalsIgnoreCase(c.name()))
        .filter(c -> usesPredefinedImage(c) || usesLocalDockerfile(c))
        .findFirst();
  }

  private static boolean usesPredefinedImage(final Component component) {
    return Strings.isNotBlank(component.container().image());
  }

  private static boolean usesLocalDockerfile(final Component component) {
    return Strings.isNotBlank(component.image().dockerfile().uri());
  }

  static ShellOptions mapOptions(final DevfileOptions options, final DevfileYaml devfile) {
    final var component = selectComponent(devfile, options.component).orElseThrow();
    final var opts = usesPredefinedImage(component)
        ? optionsForPredefinedImage(component.container())
        : optionsForLocalDockerfile(component.image());

    // A devfile session is meant to be entered interactively; a programmatically-built ShellOptions
    // defaults this to false, so it is set explicitly (picocli only defaults it for 'ilo shell').
    opts.interactive = true;
    opts.debug = options.debug;
    opts.pull = options.pull;
    opts.removeImage = options.removeImage;
    opts.runtime = options.runtime;
    opts.runtimeOptions = options.runtimeOptions;
    opts.runtimePullOptions = options.runtimePullOptions;
    // A local Dockerfile's build args (NAME=VALUE) become '--build-arg' pairs, ahead of any explicit
    // --runtime-build-option so the user's CLI value can still override the devfile's.
    opts.runtimeBuildOptions = buildOptions(options, component);
    opts.runtimeRunOptions = options.runtimeRunOptions;
    opts.runtimeCleanupOptions = options.runtimeCleanupOptions;

    return opts;
  }

  // The build options for the session: each devfile 'dockerfile.args' entry expanded to a '--build-arg'
  // pair, followed by the explicit '--runtime-build-option' values. A predefined-image component
  // contributes no Dockerfile args.
  private static List<String> buildOptions(final DevfileOptions options, final Component component) {
    final var buildOptions = new ArrayList<String>();
    if (usesLocalDockerfile(component)) {
      for (final var arg : component.image().dockerfile().args()) {
        buildOptions.add("--build-arg");
        buildOptions.add(arg);
      }
    }
    if (Objects.nonNull(options.runtimeBuildOptions)) {
      buildOptions.addAll(options.runtimeBuildOptions);
    }
    return buildOptions;
  }

  private static ShellOptions optionsForPredefinedImage(final Container container) {
    final var opts = new ShellOptions();
    opts.mountProjectDir = container.mountSources();
    opts.workingDir = container.sourceMapping();
    opts.image = container.image();
    container.env().stream()
        .filter(env -> Objects.nonNull(env.name()) && Objects.isNull(env.value()))
        .forEach(env -> System.err.println("ilo ignores the devfile environment variable '" + env.name()
            + "' because it has no value."));
    opts.variables = container.env().stream()
        .filter(env -> Objects.nonNull(env.name()) && Objects.nonNull(env.value()))
        .map(env -> String.format("%s=%s", env.name(), env.value()))
        .toList();
    // The container's command/args define its main process (PID 1). ilo's keepalive replaces that so
    // the container stays up for reuse, and attaching opens the configured shell — so the devfile
    // command/args are deliberately not carried into 'commands' (which would run on attach instead).
    return opts;
  }

  private static ShellOptions optionsForLocalDockerfile(final Image image) {
    final var opts = new ShellOptions();
    opts.mountProjectDir = true;
    opts.image = image.imageName();
    opts.containerfile = image.dockerfile().uri();
    // buildContext is optional in the devfile schema and defaults to the project root; without a
    // default the build would run with no context path and the runtime would reject it.
    opts.context = Optional.ofNullable(image.dockerfile().buildContext())
        .filter(Strings::isNotBlank)
        .orElse(".");
    return opts;
  }

}
