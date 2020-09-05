/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.test;

import wtf.metio.ilo.model.CliExecutor;
import wtf.metio.ilo.model.CliTool;
import wtf.metio.ilo.model.Runtime;

import java.util.*;

public abstract class TestCliExecutor<RUNTIME extends Runtime, CLI extends CliTool<?>> implements CliExecutor<RUNTIME, CLI> {

  private final List<List<String>> collectedArguments = new ArrayList<>(4);
  private final ArrayDeque<Integer> exitCodes = new ArrayDeque<>(4);

  @Override
  public final int execute(final List<String> arguments, final boolean debug) {
    collectedArguments.add(arguments);
    return Optional.ofNullable(exitCodes.pollFirst()).orElse(0);
  }

  public final List<String> pullArguments() {
    return collectedArguments.get(0);
  }

  public final List<String> buildArguments() {
    return collectedArguments.get(1);
  }

  public final List<String> runArguments() {
    return collectedArguments.get(2);
  }

  public final List<String> cleanupArguments() {
    return collectedArguments.get(3);
  }

  public final void exitCodes(final Integer... codes) {
    exitCodes.addAll(Arrays.asList(codes));
  }

}
