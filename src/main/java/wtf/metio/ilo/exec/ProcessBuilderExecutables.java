/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.exec;

import wtf.metio.ilo.errors.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

final class ProcessBuilderExecutables implements Executables {

  private static Process startProcess(final ProcessBuilder processBuilder) {
    try {
      return processBuilder.start();
    } catch (final UnsupportedOperationException exception) {
      throw new OperatingSystemNotSupportedException(exception);
    } catch (final NullPointerException exception) {
      throw new CommandLiistContainsNullException(exception);
    } catch (final IndexOutOfBoundsException exception) {
      throw new CommandListIsEmptyException(exception);
    } catch (final SecurityException exception) {
      throw new SecurityManagerDeniesAccessException(exception);
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  @Override
  public Optional<String> runAndReadOutput(final String[] args) {
    try {
      final var processBuilder = new ProcessBuilder(args);
      final var process = startProcess(processBuilder);
      try (final var isr = new InputStreamReader(process.getInputStream());
           final var br = new BufferedReader(isr)) {
        final var builder = new StringBuilder();
        String line;
        while (null != (line = br.readLine())) {
          builder.append(line);
          builder.append(System.lineSeparator());
        }
        return Optional.of(builder.toString());
      }
    } catch (final IOException e) {
      throw new RuntimeIOException(e);
    }
  }

  @Override
  public int runAndWaitForExit(final List<String> args) {
    try {
      return startProcess(new ProcessBuilder(args).inheritIO()).waitFor();
    } catch (final InterruptedException exception) {
      throw new UnexpectedInterruptionException(exception);
    }
  }

}
