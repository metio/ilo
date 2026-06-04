/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.devcontainer;

import wtf.metio.devcontainer.Build;
import wtf.metio.devcontainer.Devcontainer;
import wtf.metio.devcontainer.Mount;
import wtf.metio.devcontainer.MountObject;
import wtf.metio.ilo.compose.ComposeOptions;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.ilo.shell.ShellVolumeBehavior;
import wtf.metio.ilo.utils.Strings;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    // The devcontainer spec's overrideCommand defaults to true; only an explicit false opts out.
    opts.overrideCommand = !Boolean.FALSE.equals(devcontainer.overrideCommand());
    opts.shell = options.shell;
    opts.runtime = options.shellRuntime;
    opts.mountProjectDir = options.mountProjectDir;
    opts.remoteUser = user(devcontainer).orElse(null);
    // The devcontainer spec's updateRemoteUserUID defaults to true; only an explicit false opts out.
    opts.updateRemoteUserUID = !Boolean.FALSE.equals(devcontainer.updateRemoteUserUID());
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

  // containerEnv name/value pairs become '--env NAME=VALUE'. An entry without a value is dropped rather
  // than passed through as the literal 'NAME=null'.
  private static List<String> environment(final Devcontainer devcontainer) {
    return Stream.ofNullable(devcontainer.containerEnv())
        .flatMap(env -> env.entrySet().stream())
        .filter(entry -> Objects.nonNull(entry.getValue()))
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .toList();
  }

  // Each 'mounts' entry becomes a '--mount <spec>' run option. The string form already carries the
  // Docker '--mount' syntax and is forwarded verbatim; the object form is rendered from its fields so
  // the declared type is honored and a source-less (anonymous volume) entry is still emitted.
  private static List<String> mounts(final Devcontainer devcontainer) {
    return Stream.ofNullable(devcontainer.mounts())
        .flatMap(Collection::stream)
        .map(DevcontainerOptionsMapper::asMount)
        .filter(Objects::nonNull)
        .flatMap(spec -> Stream.of("--mount", spec))
        .toList();
  }

  private static String asMount(final Mount mount) {
    if (Strings.isNotBlank(mount.string())) {
      return mount.string();
    }
    return Optional.ofNullable(mount.object())
        .map(DevcontainerOptionsMapper::asMountSpec)
        .filter(Strings::isNotBlank)
        .orElse(null);
  }

  private static String asMountSpec(final MountObject mount) {
    final var fields = new ArrayList<String>();
    Optional.ofNullable(mount.type()).ifPresent(type -> fields.add("type=" + type));
    Optional.ofNullable(mount.source()).filter(Strings::isNotBlank).ifPresent(source -> fields.add("source=" + source));
    Optional.ofNullable(mount.target()).filter(Strings::isNotBlank).ifPresent(target -> fields.add("target=" + target));
    return String.join(",", fields);
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

  // runArgs are forwarded verbatim, followed by the dedicated run-configuration knobs. The run-as user
  // is not added here: it is carried by shellOptions.remoteUser and applied by the resolved user mapping
  // (which also aligns its UID/GID with the host), so it is handled once for the shell and devcontainer.
  private static List<String> runOptions(final Devcontainer devcontainer) {
    final var args = new ArrayList<String>();
    Stream.ofNullable(devcontainer.runArgs()).flatMap(Collection::stream).forEach(args::add);
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
    args.addAll(mounts(devcontainer));
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
    // dockerComposeFile entries are declared relative to the directory holding the devcontainer.json,
    // so each is resolved against that directory. The base is made absolute first so a bare filename
    // (no parent) still yields a directory to resolve against.
    final var baseDir = devcontainerJson.toAbsolutePath().getParent();
    opts.file = Stream.ofNullable(devcontainer.dockerComposeFile())
        .flatMap(Collection::stream)
        .map(baseDir::resolve)
        .map(Path::toString)
        .toList();
    opts.service = devcontainer.service();
    opts.debug = options.debug;
    opts.pull = options.pull;
    // The devcontainer spec's overrideCommand defaults to true; only an explicit false opts out.
    opts.overrideCommand = !Boolean.FALSE.equals(devcontainer.overrideCommand());
    opts.runtime = options.composeRuntime;
    return opts;
  }

  private DevcontainerOptionsMapper() {
    // utility class
  }

}
