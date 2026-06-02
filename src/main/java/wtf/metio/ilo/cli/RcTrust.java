/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.cli;

import wtf.metio.ilo.errors.RuntimeIOException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

/**
 * Persistent allowlist of trusted run command files.
 *
 * <p>A run command file is auto-discovered from the working directory and can run arbitrary commands
 * on the host when ilo expands option values (command substitution like {@code $(...)} is evaluated
 * by a host shell). A file is therefore only loaded after the user has trusted it. Trust binds the
 * file's absolute path to a hash of its content, so moving the file to another path or changing its
 * content revokes trust and forces a fresh decision.</p>
 */
public final class RcTrust {

  private RcTrust() {
    // utility class
  }

  /**
   * Records the given run command file as trusted in the user's trust store.
   *
   * @param runCommandFile The file to trust.
   */
  public static void trust(final Path runCommandFile) {
    trust(trustStore(), runCommandFile);
  }

  // visible for testing
  static boolean isTrusted(final Path store, final Path runCommandFile) {
    return readStore(store).contains(entry(runCommandFile));
  }

  /**
   * Reports whether the store already trusts the given path at <em>some</em> content. When this is
   * true but {@link #isTrusted} is false, the file has changed since it was trusted.
   *
   * @param store          The trust store to read.
   * @param runCommandFile The file to look up.
   * @return Whether any entry for this file's path exists, regardless of its content.
   */
  // visible for testing
  static boolean knowsPath(final Path store, final Path runCommandFile) {
    final var suffix = "\t" + absolute(runCommandFile);
    return readStore(store).stream().anyMatch(line -> line.endsWith(suffix));
  }

  // visible for testing
  static void trust(final Path store, final Path runCommandFile) {
    if (isTrusted(store, runCommandFile)) {
      return;
    }
    // Replace any stale entry for the same path so a re-trusted file keeps a single, current entry.
    final var suffix = "\t" + absolute(runCommandFile);
    final var entries = new ArrayList<>(readStore(store));
    entries.removeIf(line -> line.endsWith(suffix));
    entries.add(entry(runCommandFile));
    try {
      final var parent = store.toAbsolutePath().getParent();
      if (null != parent) {
        Files.createDirectories(parent);
      }
      Files.write(store, entries);
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  private static List<String> readStore(final Path store) {
    if (!Files.isReadable(store)) {
      return List.of();
    }
    try {
      return Files.readAllLines(store);
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    }
  }

  private static Path absolute(final Path runCommandFile) {
    return runCommandFile.toAbsolutePath().normalize();
  }

  // visible for testing
  static String entry(final Path runCommandFile) {
    return fingerprint(runCommandFile) + "\t" + absolute(runCommandFile);
  }

  // visible for testing
  static String fingerprint(final Path runCommandFile) {
    try {
      final var digest = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(runCommandFile));
      return HexFormat.of().formatHex(digest);
    } catch (final IOException exception) {
      throw new RuntimeIOException(exception);
    } catch (final NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 is required to be present on every JVM", exception);
    }
  }

  // visible for testing
  static Path trustStore() {
    final var override = System.getenv(EnvironmentVariables.ILO_TRUST_FILE.name());
    if (null != override && !override.isBlank()) {
      return Paths.get(override);
    }
    final var xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
    final var base = null != xdgConfigHome && !xdgConfigHome.isBlank()
        ? Paths.get(xdgConfigHome)
        : Paths.get(System.getProperty("user.home"), ".config");
    return base.resolve("ilo").resolve("trusted-rc");
  }

}
