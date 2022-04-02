/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.tools;

import wtf.metio.ilo.compose.ComposeCLI;
import wtf.metio.ilo.compose.ComposeOptions;

import java.util.List;

public final class Vagga implements ComposeCLI {

  @Override
  public String name() {
    return "vagga";
  }

  @Override
  public List<String> pullArguments(final ComposeOptions options) {
    return List.of();
  }

  @Override
  public List<String> buildArguments(final ComposeOptions options) {
    return List.of();
  }

  @Override
  public List<String> runArguments(final ComposeOptions options) {
    return List.of(name(), "_run", options.service);
  }

  @Override
  public List<String> cleanupArguments(final ComposeOptions options) {
    return List.of();
  }

}
