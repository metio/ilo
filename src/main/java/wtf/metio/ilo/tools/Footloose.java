/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.model.ComposeCLI;
import wtf.metio.ilo.compose.ComposeOptions;
import wtf.metio.ilo.cli.Debug;

import java.util.List;

public final class Footloose implements ComposeCLI {

  @Override
  public String name() {
    return "footloose";
  }

  @Override
  public List<String> pullArguments(final ComposeOptions options) {
    return List.of();
  }

  @Override
  public List<String> buildArguments(final ComposeOptions options) {
    final var args = List.of(name(), "create", "--config", options.file);
    Debug.showExecutedCommand(options.debug, args);
    return args;
  }

  @Override
  public List<String> runArguments(final ComposeOptions options) {
    final var args = List.of(name(), "ssh", "--config", options.file);
    Debug.showExecutedCommand(options.debug, args);
    return args;
  }

  @Override
  public List<String> cleanupArguments(final ComposeOptions options) {
    final var args = List.of(name(), "delete", "--config", options.file);
    Debug.showExecutedCommand(options.debug, args);
    return args;
  }

}
