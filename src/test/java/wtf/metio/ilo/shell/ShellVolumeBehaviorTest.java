/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import uk.org.webcompere.systemstubs.stream.SystemOut;
import wtf.metio.ilo.errors.LocalDirectoryDoesNotExistException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ShellVolumeBehavior")
@ExtendWith(SystemStubsExtension.class)
class ShellVolumeBehaviorTest {

  @Test
  @DisplayName("WARN: writes the warning to stderr, not stdout")
  void warnsOnStandardError(final SystemErr systemErr, final SystemOut systemOut) {
    final var directory = localDirectory("/missing");
    ShellVolumeBehavior.WARN.handleMissingDirectory(directory);
    assertAll(
        () -> assertTrue(systemErr.getText().contains("does not exist"), "warning should be on stderr"),
        () -> assertEquals("", systemOut.getText(), "nothing should be written to stdout"));
  }

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
  @DisplayName("extract local directory from a Windows drive path without splitting on the drive colon")
  void shouldExtractWindowsDriveLocalDirectory() {
    final var mount = "C:\\Users\\me:/container:ro";
    final var localPart = ShellVolumeBehavior.extractLocalPart(mount);
    assertEquals("C:\\Users\\me", localPart);
  }

  @Test
  @DisplayName("extract local directory in a mount directive without a container path")
  void shouldExtractLocalDirectoryWithoutContainerPath() {
    final var mount = "/local/directory";
    final var localPart = ShellVolumeBehavior.extractLocalPart(mount);
    assertEquals("/local/directory", localPart);
  }

  @ParameterizedTest
  @DisplayName("recognizes bind mounts by their host path source")
  @ValueSource(strings = {
      "/host:/container",
      "/host:/container:ro",
      "./relative:/container",
      "nested/path:/container",
      "~/cache:/container",
      ".:/container",
      "..:/container",
      "C:\\Users\\me:/container"
  })
  void recognizesBindMounts(final String volume) {
    assertTrue(ShellVolumeBehavior.isBindMount(volume));
  }

  @ParameterizedTest
  @DisplayName("does not treat named or anonymous volumes as bind mounts")
  @ValueSource(strings = {
      "cache:/container",
      "my-volume:/container:ro",
      "/anonymous",
      "name"
  })
  void rejectsNonBindMounts(final String volume) {
    assertFalse(ShellVolumeBehavior.isBindMount(volume));
  }

  @Test
  @DisplayName("does not manage a named volume as a local directory")
  void doesNotManageNamedVolume() {
    // A named volume has no host directory, so even ERROR must pass it through untouched.
    assertEquals(List.of("cache:/root/.cache"),
        ShellVolumeBehavior.ERROR.handleLocalDirectories(List.of("cache:/root/.cache")));
  }

  @Test
  @DisplayName("does not manage an anonymous volume as a local directory")
  void doesNotManageAnonymousVolume() {
    assertEquals(List.of("/data"),
        ShellVolumeBehavior.ERROR.handleLocalDirectories(List.of("/data")));
  }

  @Test
  @DisplayName("still manages a bind mount source")
  void stillManagesBindMount() {
    final var volumes = List.of("/no/such/host/path:/work");
    assertThrows(LocalDirectoryDoesNotExistException.class,
        () -> ShellVolumeBehavior.ERROR.handleLocalDirectories(volumes));
  }

  @Test
  @DisplayName("keeps a bind mount whose source already exists")
  void keepsExistingBindMount(@TempDir final Path directory) {
    final var volume = directory + ":/work";
    assertEquals(List.of(volume), ShellVolumeBehavior.CREATE.handleLocalDirectories(List.of(volume)));
  }

  @Test
  @DisplayName("drops a bind mount whose source is missing under WARN")
  void dropsMissingBindMountUnderWarn() {
    assertEquals(List.of(), ShellVolumeBehavior.WARN.handleLocalDirectories(List.of("/no/such/host/path:/work")));
  }

  private Path localDirectory(final String directory) {
    final var fs = Jimfs.newFileSystem(Configuration.unix());
    return fs.getPath(directory);
  }

}
