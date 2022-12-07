/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import wtf.metio.devcontainer.Build;
import wtf.metio.ilo.compose.ComposeOptions;
import wtf.metio.ilo.shell.ShellOptions;
import wtf.metio.devcontainer.Devcontainer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

final class DevcontainerOptionsMapper {

  static ShellOptions shellOptions(final DevcontainerOptions options, final Devcontainer devcontainer) {
    final var opts = new ShellOptions();
    opts.debug = options.debug;
    opts.pull = options.pull;
    opts.removeImage = options.removeImage;
    opts.runtime = options.shellRuntime;
    opts.mountProjectDir = options.mountProjectDir;
    opts.image = devcontainer.image();
    opts.context = Optional.ofNullable(devcontainer.build())
        .map(Build::context)
        .orElse(".");
    opts.containerfile = Optional.ofNullable(devcontainer.build())
        .map(Build::dockerfile)
        .orElse("");
    opts.ports = Stream.ofNullable(devcontainer.forwardPorts())
        .flatMap(Collection::stream)
        .map(port -> port + ":" + port)
        .toList();
    return opts;
  }

  static ComposeOptions composeOptions(final DevcontainerOptions options, final Devcontainer devcontainer, final Path devcontainerJson) {
    final var opts = new ComposeOptions();
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
