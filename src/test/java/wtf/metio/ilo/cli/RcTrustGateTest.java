/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemErr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RcTrustGate")
@ExtendWith(SystemStubsExtension.class)
class RcTrustGateTest {

  @ParameterizedTest
  @DisplayName("grants trust for an affirmative answer")
  @ValueSource(strings = {"y", "Y", "yes", "YES", " yes ", "a", "always", "ALWAYS"})
  void grantsForAffirmativeAnswer(final String answer) {
    assertTrue(RcTrustGate.grants(answer));
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "n", "no", "nope", "later", "yeah"})
  @DisplayName("denies trust for anything else")
  void deniesForOtherAnswer(final String answer) {
    assertFalse(RcTrustGate.grants(answer));
  }

  @Test
  @DisplayName("loads an already trusted file without prompting")
  void loadsTrustedFileWithoutPrompting(@TempDir final Path directory) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var store = directory.resolve("trusted-rc");
    RcTrust.trust(store, rc);
    final RcTrustGate.TrustPrompt failIfAsked =
        (path, changed) -> fail("prompt must not be invoked for a trusted file");

    assertTrue(new RcTrustGate(store, failIfAsked).test(rc));
  }

  @Test
  @DisplayName("remembers and loads an untrusted file once trust is granted")
  void remembersWhenGranted(@TempDir final Path directory) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var store = directory.resolve("trusted-rc");

    assertTrue(new RcTrustGate(store, (path, changed) -> true).test(rc));
    assertTrue(RcTrust.isTrusted(store, rc), "file should now be remembered");
  }

  @Test
  @DisplayName("skips and does not remember an untrusted file when trust is denied")
  void skipsWhenDenied(@TempDir final Path directory, final SystemErr systemErr) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var store = directory.resolve("trusted-rc");

    assertFalse(new RcTrustGate(store, (path, changed) -> false).test(rc));
    assertFalse(RcTrust.isTrusted(store, rc), "file must not be remembered when denied");
    assertTrue(systemErr.getText().contains("untrusted run command file"));
  }

  @Test
  @DisplayName("tells a brand-new file apart from a changed one when prompting")
  void reportsContentChangeToPrompt(@TempDir final Path directory) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var store = directory.resolve("trusted-rc");
    final var changedFlags = new java.util.ArrayList<Boolean>();
    final RcTrustGate.TrustPrompt record = (path, changed) -> {
      changedFlags.add(changed);
      return true;
    };

    new RcTrustGate(store, record).test(rc);          // first encounter: brand new
    Files.writeString(rc, "shell\n--volume\n$(evil)\n");
    new RcTrustGate(store, record).test(rc);          // same path, different content

    assertEquals(java.util.List.of(false, true), changedFlags);
  }

  @Test
  @DisplayName("notice for a new file explains it is untrusted")
  void noticeForNewFile(@TempDir final Path directory) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var notice = RcTrustGate.notice(rc, false);
    assertTrue(notice.contains("untrusted run command file"), notice);
    assertFalse(notice.contains("has changed"), notice);
  }

  @Test
  @DisplayName("notice for a changed file explains the re-trust")
  void noticeForChangedFile(@TempDir final Path directory) throws IOException {
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");
    final var notice = RcTrustGate.notice(rc, true);
    assertTrue(notice.contains("has changed since you trusted it"), notice);
  }

  @Test
  @DisplayName("treats a missing console as non-interactive")
  void missingConsoleIsNotInteractive() {
    assertFalse(RcTrustGate.hasTerminal((java.io.Console) null));
  }

  @Test
  @DisplayName("refuses an untrusted file in a non-interactive session")
  void refusesWithoutTerminal(@TempDir final Path directory, final SystemErr systemErr) throws IOException {
    // Surefire forks with redirected streams, so no real terminal is attached. Asserting this first
    // guarantees askOnConsole takes the non-interactive branch and never blocks on a prompt.
    assertFalse(RcTrustGate.hasTerminal(), "test JVM must not be treated as interactive");
    final var rc = Files.writeString(directory.resolve(".ilo.rc"), "shell\n");

    assertFalse(RcTrustGate.askOnConsole(rc, false));
    assertTrue(systemErr.getText().contains("non-interactive"));
  }

}
