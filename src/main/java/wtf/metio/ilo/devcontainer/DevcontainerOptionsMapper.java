/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import wtf.metio.ilo.compose.ComposeOptions;
import wtf.metio.ilo.shell.ShellOptions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class DevcontainerOptionsMapper {

  static ShellOptions shellOptions(final DevcontainerOptions options, final DevcontainerJson devcontainer) {
    final var opts = new ShellOptions();
    opts.debug = options.debug;
    opts.pull = options.pull;
    opts.removeImage = options.removeImage;
    opts.runtime = options.shellRuntime;
    opts.mountProjectDir = options.mountProjectDir;
    opts.image = devcontainer.image;
    opts.context = Optional.ofNullable(devcontainer.build)
        .map(build -> build.context)
        .or(() -> Optional.ofNullable(devcontainer.context))
        .orElse(".");
    opts.dockerfile = Optional.ofNullable(devcontainer.build)
        .map(build -> build.dockerFile)
        .or(() -> Optional.ofNullable(devcontainer.dockerFile))
        .orElse("");
    opts.ports = Stream.ofNullable(devcontainer.forwardPorts)
        .flatMap(List::stream)
        .map(port -> port + ":" + port)
        .collect(Collectors.toList());
    return opts;
  }

  static ComposeOptions composeOptions(final DevcontainerOptions options, final DevcontainerJson devcontainer) {
    final var opts = new ComposeOptions();
    opts.file = devcontainer.dockerComposeFile;
    opts.service = devcontainer.service;
    opts.debug = options.debug;
    opts.pull = options.pull;
    opts.runtime = options.composeRuntime;
    return opts;
  }

  private DevcontainerOptionsMapper() {
    // utility class
  }

}
