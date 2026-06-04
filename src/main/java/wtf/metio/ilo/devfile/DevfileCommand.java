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
import wtf.metio.ilo.utils.Streams;
import wtf.metio.ilo.utils.Strings;
import wtf.metio.ilo.version.VersionProvider;

import java.nio.file.Paths;
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

    return CommandLine.ExitCode.USAGE;
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

    opts.debug = options.debug;
    opts.pull = options.pull;
    opts.removeImage = options.removeImage;
    opts.runtime = options.runtime;
    opts.runtimeOptions = options.runtimeOptions;
    opts.runtimePullOptions = options.runtimePullOptions;
    opts.runtimeBuildOptions = options.runtimeBuildOptions;
    opts.runtimeRunOptions = options.runtimeRunOptions;
    opts.runtimeCleanupOptions = options.runtimeCleanupOptions;

    return opts;
  }

  private static ShellOptions optionsForPredefinedImage(final Container container) {
    final var opts = new ShellOptions();
    opts.mountProjectDir = container.mountSources();
    opts.workingDir = container.sourceMapping();
    opts.image = container.image();
    opts.variables = container.env().stream()
        .filter(env -> Objects.nonNull(env.name()) && Objects.nonNull(env.value()))
        .map(env -> String.format("%s=%s", env.name(), env.value()))
        .toList();
    opts.commands = Streams.fromLists(container.command(), container.args()).toList();
    return opts;
  }

  private static ShellOptions optionsForLocalDockerfile(final Image image) {
    final var opts = new ShellOptions();
    opts.mountProjectDir = true;
    opts.image = image.imageName();
    opts.containerfile = image.dockerfile().uri();
    opts.context = image.dockerfile().buildContext();
    return opts;
  }

}
