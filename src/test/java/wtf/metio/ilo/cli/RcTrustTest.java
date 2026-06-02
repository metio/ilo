/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RcTrust")
@ExtendWith(SystemStubsExtension.class)
class RcTrustTest {

  @Test
  @DisplayName("fingerprints identical content identically")
  void fingerprintsContentStably(@TempDir final Path directory) throws IOException {
    final var a = Files.writeString(directory.resolve("a.rc"), "shell\nfedora:latest\n");
    final var b = Files.writeString(directory.resolve("b.rc"), "shell\nfedora:latest\n");
    assertEquals(RcTrust.fingerprint(a), RcTrust.fingerprint(b));
  }

  @Test
  @DisplayName("fingerprints different content differently")
  void fingerprintsContentDistinctly(@TempDir final Path directory) throws IOException {
    final var a = Files.writeString(directory.resolve("a.rc"), "shell\nfedora:latest\n");
    final var b = Files.writeString(directory.resolve("b.rc"), "shell\nalpine:latest\n");
    assertNotEquals(RcTrust.fingerprint(a), RcTrust.fingerprint(b));
  }

  @Test
  @DisplayName("an entry contains the fingerprint and the absolute path")
  void entryContainsFingerprintAndPath(@TempDir final Path directory) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var entry = RcTrust.entry(rc);
    assertTrue(entry.startsWith(RcTrust.fingerprint(rc)));
    assertTrue(entry.endsWith(rc.toAbsolutePath().normalize().toString()));
  }

  @Test
  @DisplayName("reports an unknown file as untrusted when the store is missing")
  void untrustedWhenStoreMissing(@TempDir final Path directory) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    assertFalse(RcTrust.isTrusted(directory.resolve("missing-store"), rc));
  }

  @Test
  @DisplayName("trusts a file and then recognizes it")
  void trustsAndRecognizes(@TempDir final Path directory) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var store = directory.resolve("store/trusted-rc");

    assertFalse(RcTrust.isTrusted(store, rc));
    RcTrust.trust(store, rc);
    assertTrue(RcTrust.isTrusted(store, rc));
  }

  @Test
  @DisplayName("revokes trust once the file content changes")
  void revokesTrustOnContentChange(@TempDir final Path directory) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var store = directory.resolve("trusted-rc");
    RcTrust.trust(store, rc);

    Files.writeString(rc, "shell\n--volume\n$(evil)\n");
    assertFalse(RcTrust.isTrusted(store, rc));
  }

  @Test
  @DisplayName("does not record a file twice")
  void trustIsIdempotent(@TempDir final Path directory) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var store = directory.resolve("trusted-rc");

    RcTrust.trust(store, rc);
    RcTrust.trust(store, rc);

    assertEquals(1, Files.readAllLines(store).size());
  }

  @Test
  @DisplayName("knows a path it has seen even after the content changes")
  void knowsPathAcrossContentChanges(@TempDir final Path directory) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var store = directory.resolve("trusted-rc");

    assertFalse(RcTrust.knowsPath(store, rc), "unknown before trusting");
    RcTrust.trust(store, rc);
    Files.writeString(rc, "shell\n--volume\n$(evil)\n");

    assertFalse(RcTrust.isTrusted(store, rc), "changed content is no longer trusted");
    assertTrue(RcTrust.knowsPath(store, rc), "but the path is still known");
  }

  @Test
  @DisplayName("does not know an unrelated path")
  void doesNotKnowUnrelatedPath(@TempDir final Path directory) throws IOException {
    final var trusted = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var other = Files.writeString(directory.resolve("other.rc"), "shell\n");
    final var store = directory.resolve("trusted-rc");
    RcTrust.trust(store, trusted);

    assertFalse(RcTrust.knowsPath(store, other));
  }

  @Test
  @DisplayName("replaces the stale entry when a changed file is re-trusted")
  void reTrustReplacesStaleEntry(@TempDir final Path directory) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var store = directory.resolve("trusted-rc");
    RcTrust.trust(store, rc);

    Files.writeString(rc, "shell\nalpine:latest\n");
    RcTrust.trust(store, rc);

    assertEquals(1, Files.readAllLines(store).size(), "one entry per path");
    assertTrue(RcTrust.isTrusted(store, rc), "the current content is trusted");
  }

  @Test
  @DisplayName("re-trusting one file keeps other trusted files")
  void reTrustKeepsOtherEntries(@TempDir final Path directory) throws IOException {
    final var changing = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var other = Files.writeString(directory.resolve("other.rc"), "shell\nalpine:latest\n");
    final var store = directory.resolve("trusted-rc");
    RcTrust.trust(store, changing);
    RcTrust.trust(store, other);

    Files.writeString(changing, "shell\nfedora:latest\n");
    RcTrust.trust(store, changing);

    assertTrue(RcTrust.isTrusted(store, other), "the unrelated entry must be preserved");
    assertTrue(RcTrust.isTrusted(store, changing), "the re-trusted file is trusted");
    assertEquals(2, Files.readAllLines(store).size());
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("uses ILO_TRUST_FILE when set")
  void usesTrustFileOverride(final EnvironmentVariables environment) {
    environment.set(wtf.metio.ilo.cli.EnvironmentVariables.ILO_TRUST_FILE.name(), "/tmp/custom-trust");
    assertEquals(Paths.get("/tmp/custom-trust"), RcTrust.trustStore());
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("falls back to XDG_CONFIG_HOME when no override is set")
  void usesXdgConfigHome(final EnvironmentVariables environment) {
    environment.set(wtf.metio.ilo.cli.EnvironmentVariables.ILO_TRUST_FILE.name(), "");
    environment.set("XDG_CONFIG_HOME", "/home/user/.config");
    assertEquals(Paths.get("/home/user/.config/ilo/trusted-rc"), RcTrust.trustStore());
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("falls back to the home directory when XDG_CONFIG_HOME is unset")
  void usesHomeDirectory(final EnvironmentVariables environment, final SystemProperties properties) {
    environment.set(wtf.metio.ilo.cli.EnvironmentVariables.ILO_TRUST_FILE.name(), "");
    environment.set("XDG_CONFIG_HOME", "");
    properties.set("user.home", "/home/user");
    assertEquals(Paths.get("/home/user/.config/ilo/trusted-rc"), RcTrust.trustStore());
  }

}
