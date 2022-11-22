/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devfile;

import picocli.CommandLine;
import wtf.metio.ilo.shell.Shell;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.ilo.utils.Streams;
import wtf.metio.ilo.utils.Strings;
import wtf.metio.ilo.version.VersionProvider;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

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
public final class Devfile implements Callable<Integer> {

  @CommandLine.Mixin
  public DevfileOptions options;

  @Override
  public Integer call() {
    final var currentDir = Paths.get(System.getProperty("user.dir"));
    final var yaml = findDevfile(currentDir, options.locations);
    final var devfile = parseDevfile(yaml);

    if (hasSupportedDevfileConfiguration(devfile, options.component)) {
      final var command = new Shell();
      command.options = mapOptions(options, devfile);
      return command.call();
    }

    return CommandLine.ExitCode.USAGE;
  }

  static boolean hasSupportedDevfileConfiguration(final DevfileYaml devfile, final String component) {
    return Stream.ofNullable(devfile.components)
        .flatMap(Collection::stream)
        .filter(c -> c.name.equalsIgnoreCase(component))
        .anyMatch(c -> usesPredefinedImage(c) || usesLocalDockerfile(c));
  }

  private static boolean usesPredefinedImage(final DevfileYaml.Component component) {
    return Objects.nonNull(component.container) && Strings.isNotBlank(component.container.image);
  }

  private static boolean usesLocalDockerfile(final DevfileYaml.Component component) {
    return Objects.nonNull(component.image) &&
        Objects.nonNull(component.image.dockerfile) &&
        Strings.isNotBlank(component.image.dockerfile.uri);
  }

  static ShellOptions mapOptions(final DevfileOptions options, final DevfileYaml devfile) {
    final var opts = Stream.ofNullable(devfile.components)
        .flatMap(Collection::stream)
        .filter(Devfile::usesPredefinedImage)
        .map(component -> component.container)
        .findFirst()
        .map(Devfile::optionsForPredefinedImage)
        .or(() -> Stream.ofNullable(devfile.components)
            .flatMap(Collection::stream)
            .filter(Devfile::usesLocalDockerfile)
            .map(component -> component.image)
            .findFirst()
            .map(Devfile::optionsForLocalDockerfile))
        .orElseThrow();

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

  private static ShellOptions optionsForPredefinedImage(final DevfileYaml.Container container) {
    final var opts = new ShellOptions();
    opts.mountProjectDir = container.mountSources;
    opts.workingDir = container.sourceMapping;
    opts.image = container.image;
    opts.variables = Stream.ofNullable(container.env)
        .flatMap(Collection::stream)
        .map(env -> String.format("%s=%s", env.name, env.value))
        .toList();
    opts.commands = Streams.fromLists(container.command, container.args).toList();
    return opts;
  }

  private static ShellOptions optionsForLocalDockerfile(final DevfileYaml.Image image) {
    final var opts = new ShellOptions();
    opts.mountProjectDir = true;
    opts.image = image.imageName;
    opts.containerfile = image.dockerfile.uri;
    opts.context = image.dockerfile.buildContext;
    return opts;
  }

}
