/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.compose.ComposeCLI;
import wtf.metio.ilo.compose.ComposeOptions;
import wtf.metio.ilo.os.OSSupport;

import java.util.List;

import static java.util.stream.Stream.of;
import static wtf.metio.ilo.utils.Streams.*;
import static wtf.metio.ilo.utils.Streams.fromList;

/**
 * Support for 'docker compose' aka 'Compose V2'
 *
 * @see <a href="https://docs.docker.com/compose/cli-command/">official documentation</a>
 */
public final class DockerCompose2 extends DockerComposeLike {

  @Override
  public String name() {
    return "docker";
  }

  @Override
  public String command() {
    return "compose";
  }

}
