/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import wtf.metio.ilo.errors.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

/**
 * Utility class that interacts with executables found on the host machine.
 */
public final class Executables {

  /**
   * Resolves a tool by its name from the current $PATH. To match shell behavior, the first match will be returned.
   * Thus make sure to order your $PATH so that your preferred location will be picked first.
   *
   * @param tool The name of the tool to look up.
   * @return The path to the tool or an empty optional.
   */
  public static Optional<Path> of(final String tool) {
    final var names = candidateNames(tool, isWindows(), executableExtensions());
    return allPaths()
        .flatMap(directory -> names.stream().map(directory::resolve))
        .filter(Executables::canExecute)
        .findFirst();
  }

  /** Whether ilo is running on a Windows host, decided from the {@code os.name} system property. */
  public static boolean isWindows() {
    return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows");
  }

  /**
   * Computes the file names to probe for a tool. On Windows an executable is found under its base
   * name plus an extension from {@code PATHEXT} (e.g. {@code docker.exe}), so a bare name like
   * {@code docker} would never match. A name that already carries an extension is used verbatim.
   *
   * @param tool       The tool name as requested, e.g. 'docker'.
   * @param windows    Whether the host is running Windows.
   * @param extensions The executable extensions to append on Windows, e.g. '.EXE'.
   * @return The candidate file names to look up on $PATH, in preference order.
   */
  // visible for testing
  static List<String> candidateNames(final String tool, final boolean windows, final List<String> extensions) {
    if (windows && !hasExtension(tool)) {
      return extensions.stream()
          .map(extension -> tool + extension)
          .toList();
    }
    return List.of(tool);
  }

  // visible for testing
  static boolean hasExtension(final String tool) {
    final var fileName = Paths.get(tool).getFileName().toString();
    return 0 < fileName.lastIndexOf('.');
  }

  // visible for testing
  static List<String> executableExtensions() {
    return parseExtensions(System.getenv("PATHEXT"));
  }

  // visible for testing
  static List<String> parseExtensions(final String pathext) {
    final var raw = null == pathext || pathext.isBlank() ? ".COM;.EXE;.BAT;.CMD" : pathext;
    return Arrays.stream(raw.split(";"))
        .map(String::trim)
        .filter(not(String::isBlank))
        .toList();
  }

  // visible for testing
  static Stream<Path> allPaths() {
    return allPaths(System.getenv("PATH"));
  }

  // visible for testing
  static Stream<Path> allPaths(final String path) {
    if (null == path) {
      return Stream.empty();
    }
    return Stream.of(path.split(Pattern.quote(File.pathSeparator)))
        .map(Paths::get);
  }

  // visible for testing
  static boolean canExecute(final Path binary) {
    return Files.exists(binary) && Files.isExecutable(binary);
  }

  public static int runAndWaitForExit(final List<String> arguments, final boolean debug) {
    if (null == arguments || arguments.isEmpty()) {
      return 0;
    }
    if (debug) {
      System.err.println("ilo executes: " + String.join(" ", arguments));
    }
    try {
      return new ProcessBuilder(arguments).inheritIO().start().waitFor();
    } catch (final InterruptedException exception) {
      Thread.currentThread().interrupt();
      throw new UnexpectedInterruptionException(exception);
    } catch (final UnsupportedOperationException exception) {
      throw new OperatingSystemNotSupportedException(exception);
    } catch (final NullPointerException exception) {
      throw new CommandListContainsNullException(exception, arguments);
    } catch (final IndexOutOfBoundsException exception) {
      throw new CommandListIsEmptyException(exception);
    } catch (final SecurityException exception) {
      throw new SecurityManagerDeniesAccessException(exception);
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  /**
   * Runs a command capturing its combined stdout/stderr instead of inheriting the terminal, returning
   * the exit code and that output. Used for lifecycle commands that run in parallel, so each one's
   * output can be printed as a single block rather than interleaving on the terminal. Stdin is closed
   * (these commands are non-interactive), and there is no timeout — setup commands may legitimately run
   * for a long time.
   *
   * @param arguments The command line to run.
   * @param debug     Whether to echo the command line first.
   * @return The exit code and captured combined output.
   */
  public static SessionLifecycle.CommandResult runAndCapture(final List<String> arguments, final boolean debug) {
    return runAndCapture(CAPTURE_DRAIN_GRACE, arguments, debug);
  }

  // The command itself has no timeout (setup commands may legitimately run long), but once it has
  // exited a backgrounded child can keep its stdout pipe open; draining is bounded by this grace so
  // ilo returns the already-known exit code instead of blocking on the held pipe forever.
  static final Duration CAPTURE_DRAIN_GRACE = Duration.ofSeconds(10);

  // visible for testing
  static SessionLifecycle.CommandResult runAndCapture(final Duration drainGrace, final List<String> arguments, final boolean debug) {
    if (null == arguments || arguments.isEmpty()) {
      return new SessionLifecycle.CommandResult(0, "");
    }
    if (debug) {
      System.err.println("ilo executes: " + String.join(" ", arguments));
    }
    try {
      final var process = new ProcessBuilder(arguments)
          .redirectErrorStream(true)
          .start();
      // Non-interactive: closing stdin gives the command an immediate EOF rather than the parent's.
      process.getOutputStream().close();
      // Drain stdout on a separate daemon thread so a child that holds the pipe open after the command
      // exits cannot block ilo forever; the result is published only once readAllBytes returns, so the
      // main thread reads it only after the reader is done (or the grace lapses, leaving it empty).
      final var captured = new AtomicReference<>("");
      final var reader = new Thread(() -> {
        try (final var stream = process.getInputStream()) {
          captured.set(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        } catch (final IOException _) {
          // the stream is closed when the process is destroyed; nothing to capture
        }
      }, "ilo-capture-reader");
      reader.setDaemon(true);
      reader.start();
      final var exitCode = process.waitFor();
      reader.join(drainGrace.toMillis());
      return new SessionLifecycle.CommandResult(exitCode, captured.get());
    } catch (final InterruptedException exception) {
      Thread.currentThread().interrupt();
      throw new UnexpectedInterruptionException(exception);
    } catch (final UnsupportedOperationException exception) {
      throw new OperatingSystemNotSupportedException(exception);
    } catch (final NullPointerException exception) {
      throw new CommandListContainsNullException(exception, arguments);
    } catch (final IndexOutOfBoundsException exception) {
      throw new CommandListIsEmptyException(exception);
    } catch (final SecurityException exception) {
      throw new SecurityManagerDeniesAccessException(exception);
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  // The expansion commands ilo runs (command substitution, parameter expansion) are expected to be
  // near-instant; this bounds a runaway or stuck command so ilo cannot hang indefinitely.
  static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

  public static String runAndReadOutput(final String... arguments) {
    return runAndReadOutput(DEFAULT_TIMEOUT, arguments);
  }

  // visible for testing
  static String runAndReadOutput(final Duration timeout, final String... arguments) {
    if (null == arguments || 0 == arguments.length) {
      return "";
    }
    return run(timeout, arguments).output().strip();
  }

  // A presence probe (is an optional plugin/tool installed?) answers quickly when healthy, so a longer
  // wait means the target is missing, broken, or hanging. This bounds it tighter than the expansion
  // timeout so a stuck probe (e.g. 'docker compose version' against an unresponsive daemon) cannot stall
  // ilo for the full default timeout.
  static final Duration PROBE_TIMEOUT = Duration.ofSeconds(10);

  /**
   * Reads a command's output for a best-effort presence probe. Unlike {@link #runAndReadOutput} it never
   * throws: a probe that times out or whose binary cannot be started yields no output, so a missing,
   * broken, or hanging target reads as simply absent rather than aborting the run.
   *
   * @param arguments The probe command line to run.
   * @return The command's output, or an empty string if it could not be run to completion.
   */
  public static String probeOutput(final String... arguments) {
    return probeOutput(PROBE_TIMEOUT, arguments);
  }

  // visible for testing
  static String probeOutput(final Duration timeout, final String... arguments) {
    try {
      return runAndReadOutput(timeout, arguments);
    } catch (final CommandTimedOutException | RuntimeIOException exception) {
      return "";
    }
  }

  /**
   * Runs a host-shell expansion command (command substitution or parameter expansion) and returns its
   * output with trailing newlines removed — matching how a shell's {@code $(...)} trims — while, unlike
   * {@link #runAndReadOutput}, preserving the leading and interior whitespace a value legitimately
   * contains. A non-zero exit is reported on stderr (the command's own error is already there) and its
   * output is used as-is, so a failed expansion is surfaced rather than silently swallowed.
   *
   * @param arguments The command line to run.
   * @return The command's output with trailing newlines removed.
   */
  public static String runForExpansion(final String... arguments) {
    return runForExpansion(DEFAULT_TIMEOUT, arguments);
  }

  // visible for testing
  static String runForExpansion(final Duration timeout, final String... arguments) {
    if (null == arguments || 0 == arguments.length) {
      return "";
    }
    final var result = run(timeout, arguments);
    if (0 != result.exitCode()) {
      System.err.println("ilo: expansion command exited with status " + result.exitCode()
          + "; using its output as-is: " + String.join(" ", arguments));
    }
    return stripTrailingNewlines(result.output());
  }

  private static String stripTrailingNewlines(final String value) {
    var end = value.length();
    while (end > 0 && ('\n' == value.charAt(end - 1) || '\r' == value.charAt(end - 1))) {
      end--;
    }
    return value.substring(0, end);
  }

  // Runs a command, draining its stdout on a separate thread, and returns the exit code with the raw
  // (untrimmed) output. Stderr is inherited rather than captured: it never fills a pipe ilo would have
  // to drain (so a command that floods stderr cannot deadlock), and the user still sees the errors.
  private static Captured run(final Duration timeout, final String... arguments) {
    final Process process;
    try {
      process = new ProcessBuilder(arguments)
          .redirectError(ProcessBuilder.Redirect.INHERIT)
          .start();
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }

    // Drain stdout on a separate thread so a stuck producer can be timed out instead of blocking a
    // read forever, and so a full stdout pipe never blocks the command either. The result is only
    // published once readAllBytes returns, so it is read on the main thread only after the reader
    // has finished — never concurrently.
    final var captured = new AtomicReference<>("");
    final var reader = new Thread(() -> {
      try (final var stream = process.getInputStream()) {
        captured.set(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
      } catch (final IOException _) {
        // the stream is closed when the process is destroyed; nothing to capture
      }
    }, "ilo-output-reader");
    reader.setDaemon(true);
    reader.start();

    final var deadline = System.nanoTime() + timeout.toNanos();
    try {
      if (!process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
        process.destroyForcibly();
        throw new CommandTimedOutException(timeout.toSeconds(), String.join(" ", arguments));
      }
      // Drain whatever the process wrote before it exited, but only for the time left in the overall
      // timeout — so the total wait stays bounded by 'timeout' rather than up to twice it. The floor of
      // 1ms matters because join(0) would block forever.
      final var remaining = Math.max(1L, (deadline - System.nanoTime()) / 1_000_000L);
      reader.join(remaining);
    } catch (final InterruptedException exception) {
      process.destroyForcibly();
      Thread.currentThread().interrupt();
      throw new UnexpectedInterruptionException(exception);
    }
    if (reader.isAlive()) {
      // The process exited but its stdout is still held open (e.g. by a backgrounded child) past the
      // timeout. Give up rather than read the output while the reader thread may still be writing it.
      process.destroyForcibly();
      throw new CommandTimedOutException(timeout.toSeconds(), String.join(" ", arguments));
    }
    return new Captured(process.exitValue(), captured.get());
  }

  private record Captured(int exitCode, String output) {
  }

  private Executables() {
    // utility class
  }

}
