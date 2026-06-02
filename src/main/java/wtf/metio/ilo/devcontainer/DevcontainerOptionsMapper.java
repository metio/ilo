/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.devcontainer;

import wtf.metio.devcontainer.Build;
import wtf.metio.devcontainer.Devcontainer;
import wtf.metio.ilo.compose.ComposeOptions;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.ilo.shell.ShellVolumeBehavior;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

final class DevcontainerOptionsMapper {

  static ShellOptions shellOptions(final DevcontainerOptions options, final Devcontainer devcontainer) {
    final var opts = new ShellOptions();
    opts.interactive = true;
    opts.missingVolumes = ShellVolumeBehavior.CREATE;
    opts.debug = options.debug;
    opts.pull = options.pull;
    opts.removeImage = options.removeImage;
    opts.fresh = options.fresh;
    opts.keepRunning = options.keepRunning;
    opts.shell = options.shell;
    opts.runtime = options.shellRuntime;
    opts.mountProjectDir = options.mountProjectDir;
    opts.image = devcontainer.image();
    opts.workingDir = devcontainer.workspaceFolder();
    opts.context = Optional.ofNullable(devcontainer.build())
        .map(Build::context)
        .orElse(".");
    opts.containerfile = Optional.ofNullable(devcontainer.build())
        .map(Build::dockerfile)
        .orElse("");
    opts.ports = ports(devcontainer);
    opts.variables = environment(devcontainer);
    opts.volumes = mounts(devcontainer);
    opts.runtimeBuildOptions = buildOptions(devcontainer.build());
    opts.runtimeRunOptions = runOptions(devcontainer);
    return opts;
  }

  // forwardPorts are published 1:1 (host == container); appPort entries are passed through verbatim.
  private static List<String> ports(final Devcontainer devcontainer) {
    return Stream.concat(
            Stream.ofNullable(devcontainer.forwardPorts()).flatMap(Collection::stream).map(port -> port + ":" + port),
            Stream.ofNullable(devcontainer.appPort()).flatMap(Collection::stream))
        .toList();
  }

  // containerEnv name/value pairs become '--env NAME=VALUE'.
  private static List<String> environment(final Devcontainer devcontainer) {
    return Stream.ofNullable(devcontainer.containerEnv())
        .flatMap(env -> env.entrySet().stream())
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .toList();
  }

  // 'mounts' entries (objects with a source and target) become '--volume source:target'.
  private static List<String> mounts(final Devcontainer devcontainer) {
    return Stream.ofNullable(devcontainer.mounts())
        .flatMap(Collection::stream)
        .map(DevcontainerOptionsMapper::asVolume)
        .filter(Objects::nonNull)
        .toList();
  }

  private static String asVolume(final Map<String, String> mount) {
    final var source = mount.get("source");
    final var target = mount.get("target");
    return Objects.nonNull(source) && Objects.nonNull(target) ? source + ":" + target : null;
  }

  // build.args -> --build-arg NAME=VALUE, build.target -> --target, build.cacheFrom -> --cache-from.
  private static List<String> buildOptions(final Build build) {
    final var args = new ArrayList<String>();
    if (Objects.nonNull(build)) {
      Optional.ofNullable(build.args()).ifPresent(map -> map.forEach((key, value) -> {
        args.add("--build-arg");
        args.add(key + "=" + value);
      }));
      Optional.ofNullable(build.target()).filter(target -> !target.isBlank()).ifPresent(target -> {
        args.add("--target");
        args.add(target);
      });
      Stream.ofNullable(build.cacheFrom()).flatMap(Collection::stream).forEach(cache -> {
        args.add("--cache-from");
        args.add(cache);
      });
    }
    return args;
  }

  // runArgs are forwarded verbatim, followed by the dedicated run-configuration knobs.
  private static List<String> runOptions(final Devcontainer devcontainer) {
    final var args = new ArrayList<String>();
    Stream.ofNullable(devcontainer.runArgs()).flatMap(Collection::stream).forEach(args::add);
    user(devcontainer).ifPresent(user -> {
      args.add("--user");
      args.add(user);
    });
    if (Boolean.TRUE.equals(devcontainer.init())) {
      args.add("--init");
    }
    if (Boolean.TRUE.equals(devcontainer.privileged())) {
      args.add("--privileged");
    }
    Stream.ofNullable(devcontainer.capAdd()).flatMap(Collection::stream).forEach(capability -> {
      args.add("--cap-add");
      args.add(capability);
    });
    Stream.ofNullable(devcontainer.securityOpt()).flatMap(Collection::stream).forEach(option -> {
      args.add("--security-opt");
      args.add(option);
    });
    return args;
  }

  // containerUser overrides all in-container operations; remoteUser is the fallback for the shell.
  private static Optional<String> user(final Devcontainer devcontainer) {
    return Optional.ofNullable(devcontainer.containerUser())
        .filter(user -> !user.isBlank())
        .or(() -> Optional.ofNullable(devcontainer.remoteUser()).filter(user -> !user.isBlank()));
  }

  static ComposeOptions composeOptions(final DevcontainerOptions options, final Devcontainer devcontainer, final Path devcontainerJson) {
    final var opts = new ComposeOptions();
    opts.interactive = true;
    opts.file = Stream.ofNullable(devcontainer.dockerComposeFile())
        .flatMap(Collection::stream)
        .map(Paths::get)
        .map(devcontainerJson::relativize)
        .map(Path::toAbsolutePath)
        .map(Path::toString)
        .toList();
    opts.service = devcontainer.service();
    opts.debug = options.debug;
    opts.pull = options.pull;
    opts.runtime = options.composeRuntime;
    return opts;
  }

  private DevcontainerOptionsMapper() {
    // utility class
  }

}
