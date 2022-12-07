/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.shell;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wtf.metio.ilo.errors.LocalDirectoryDoesNotExistException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ShellVolumeBehavior")
class ShellVolumeBehaviorTest {

  @ParameterizedTest
  @DisplayName("defines behavior")
  @ValueSource(strings = {
      "CREATE",
      "WARN",
      "ERROR"
  })
  void shouldHaveBehavior(final String runtime) {
    assertNotNull(ShellVolumeBehavior.valueOf(runtime));
  }

  @Test
  @DisplayName("CREATE: handle missing")
  void shouldCreateMissingDirectory() {
    final var directory = localDirectory("/missing");
    final var ok = ShellVolumeBehavior.CREATE.handleMissingDirectory(directory);
    assertAll(
        () -> assertTrue(ok, "Missing directory could not be created"),
        () -> assertTrue(Files.exists(directory), "Directory was not actually created"));
  }

  @Test
  @DisplayName("CREATE: handle existing")
  void shouldIgnoreExistingDirectory() throws IOException {
    final var directory = localDirectory("/existing");
    Files.createDirectory(directory);
    final var ok = ShellVolumeBehavior.CREATE.handleMissingDirectory(directory);
    assertTrue(ok, "Existing directory was not ignored");
  }

  @Test
  @DisplayName("WARN: handle missing")
  void shouldWarnOnMissingDirectory() {
    final var directory = localDirectory("/missing");
    final var ok = ShellVolumeBehavior.WARN.handleMissingDirectory(directory);
    assertAll(
        () -> assertFalse(ok, "No warning for missing directory"),
        () -> assertTrue(Files.notExists(directory), "Directory was actually created"));
  }

  @Test
  @DisplayName("WARN: handle existing")
  void shouldIgnoreExistingDirectoryWarn() throws IOException {
    final var directory = localDirectory("/existing");
    Files.createDirectory(directory);
    final var ok = ShellVolumeBehavior.WARN.handleMissingDirectory(directory);
    assertTrue(ok, "Existing directory was not ignored");
  }

  @Test
  @DisplayName("ERROR: handle missing")
  void shouldThrowOnMissingDirectory() {
    final var directory = localDirectory("/missing");
    assertAll(
        () -> assertThrows(LocalDirectoryDoesNotExistException.class,
            () -> ShellVolumeBehavior.ERROR.handleMissingDirectory(directory)),
        () -> assertTrue(Files.notExists(directory), "Directory was actually created"));
  }

  @Test
  @DisplayName("ERROR: handle existing")
  void shouldIgnoreExistingDirectoryError() throws IOException {
    final var directory = localDirectory("/existing");
    Files.createDirectory(directory);
    final var ok = ShellVolumeBehavior.ERROR.handleMissingDirectory(directory);
    assertTrue(ok, "Existing directory was not ignored");
  }

  @Test
  @DisplayName("extract local directory")
  void shouldExtractLocalDirectory() {
    final var mount = "/local/directory:/container/directory";
    final var localPart = ShellVolumeBehavior.extractLocalPart(mount);
    assertEquals("/local/directory", localPart);
  }

  @Test
  @DisplayName("extract local directory in a mount directive using a SEL label")
  void shouldExtractLocalDirectoryWithSelLabel() {
    final var mount = "/local/directory:/container/directory:Z";
    final var localPart = ShellVolumeBehavior.extractLocalPart(mount);
    assertEquals("/local/directory", localPart);
  }

  @Test
  @DisplayName("extract local directory in a mount directive without a container path")
  void shouldExtractLocalDirectoryWithoutContainerPath() {
    final var mount = "/local/directory";
    final var localPart = ShellVolumeBehavior.extractLocalPart(mount);
    assertEquals("/local/directory", localPart);
  }

  private Path localDirectory(final String directory) {
    final var fs = Jimfs.newFileSystem(Configuration.unix());
    return fs.getPath(directory);
  }

}
