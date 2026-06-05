/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;
import uk.org.webcompere.systemstubs.stream.SystemErr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RemoteUser")
@ExtendWith(SystemStubsExtension.class)
class RemoteUserTest {

  // Distinguishes the usermod/groupmod probe (reports the tools are present), the inspect probe
  // (returns the image's user), the 'docker info' probe (reports a rootful daemon), and the 'id' probe
  // (reports the user's UID/GID); anything else returns nothing.
  private static final Function<List<String>, String> CAPTURE = args -> {
    if (args.stream().anyMatch(arg -> arg.contains("usermod"))) {
      return "ok";
    }
    if (args.contains("inspect")) {
      return "vscode";
    }
    if (args.contains("info")) {
      return "[name=seccomp,profile=builtin]";
    }
    if (args.contains("--entrypoint")) {
      return "uid=1000(node) gid=1000(node) groups=1000(node)";
    }
    return "";
  };

  @Test
  @DisplayName("maps nothing when the feature is disabled")
  void disabled() {
    final var options = options(false, "node", "image:test");
    RemoteUser.resolve(new Podman(), options, CAPTURE);
    assertEquals(RemoteUserMapping.NONE, options.userMapping);
  }

  @Test
  @DisplayName("keeps a non-root user with a keep-id namespace on podman and leaves the image alone")
  void podmanKeepId() {
    final var options = options(true, "node", "image:test");
    RemoteUser.resolve(new Podman(), options, CAPTURE);
    assertEquals(RemoteUserMapping.KEEP_ID, options.userMapping);
    assertNull(options.containerfile, "no derived image is built for keep-id");
  }

  @Test
  @DisplayName("does not probe the user's IDs during resolution")
  void resolveDoesNotProbeIds() {
    final var options = options(true, "node", "image:test");
    RemoteUser.resolve(new Podman(), options, CAPTURE);
    assertNull(options.remoteUid, "the id probe is deferred to create-time");
  }

  @Test
  @DisplayName("pins the user's UID/GID for a podman keep-id mapping when creating")
  void pinsKeepIdIds() {
    final var options = keepId("node", "image:test");
    RemoteUser.pinKeepId(new Podman(), options, CAPTURE, true);
    assertEquals("1000", options.remoteUid);
    assertEquals("1000", options.remoteGid);
  }

  @Test
  @DisplayName("does not probe the user's IDs when reusing an existing container")
  void skipsKeepIdProbeWhenReusing() {
    final var options = keepId("node", "image:test");
    RemoteUser.pinKeepId(new Podman(), options, CAPTURE, false);
    assertNull(options.remoteUid);
  }

  @Test
  @DisplayName("does not pin IDs on nerdctl, which has no keep-id:uid support")
  void skipsKeepIdProbeOnNerdctl() {
    final var options = keepId("node", "image:test");
    RemoteUser.pinKeepId(new Nerdctl(), options, CAPTURE, true);
    assertNull(options.remoteUid);
  }

  @Test
  @DisplayName("does not pin IDs when the mapping is not keep-id")
  void skipsKeepIdProbeForOtherMappings() {
    final var options = options(true, "node", "image:test");
    options.userMapping = RemoteUserMapping.NONE;
    RemoteUser.pinKeepId(new Podman(), options, CAPTURE, true);
    assertNull(options.remoteUid);
  }

  @Test
  @DisplayName("builds a derived image to remap a non-root user on rootful Docker")
  void rootfulDockerRemap() {
    final var options = options(true, "node", "python:3");
    RemoteUser.resolve(new Docker(), options, CAPTURE);
    assertEquals(RemoteUserMapping.REMAP, options.userMapping);
    assertTrue(options.image.startsWith("ilo-remote-user:"), options.image);
    assertTrue(options.containerfile != null, "a derived containerfile is generated");
  }

  @Test
  @DisplayName("does nothing for a root user on podman, neither probing nor building")
  void podmanRootIsNoop() {
    final var options = options(true, "root", "image:test");
    RemoteUser.resolve(new Podman(), options, CAPTURE);
    assertEquals(RemoteUserMapping.NONE, options.userMapping);
    assertNull(options.remoteUid, "no id probe for the root user");
    assertNull(options.containerfile, "no derived image is built");
  }

  @Test
  @DisplayName("detects the image's user when --remote-user is omitted")
  void autoDetectsUser() {
    final var options = options(true, null, "image:test");
    RemoteUser.resolve(new Podman(), options, CAPTURE);
    assertEquals("vscode", options.remoteUser);
    assertEquals(RemoteUserMapping.KEEP_ID, options.userMapping);
  }

  @Test
  @DisplayName("builds the derived image when the remap can be performed")
  void remapBuildsDerivedImage() {
    final var options = options(true, "node", "python:3");
    assertEquals(RemoteUserMapping.REMAP, RemoteUser.prepareRemap(new Docker(), options, CAPTURE, "1000", "1000"));
    assertTrue(options.image.startsWith("ilo-remote-user:"), options.image);
  }

  @Test
  @DisplayName("falls back to the host user when the image lacks usermod/groupmod")
  void remapWithoutUserToolsFallsBackToHostUser() {
    final var options = options(true, "node", "python:3");
    assertEquals(RemoteUserMapping.HOST_USER, RemoteUser.prepareRemap(new Docker(), options, args -> "", "1000", "1000"));
    assertNull(options.containerfile, "no derived image is built");
  }

  @Test
  @DisplayName("falls back to the host user for a user name that cannot be safely remapped")
  void remapWithUnsafeUserFallsBackToHostUser() {
    final var options = options(true, "bad user; rm", "python:3");
    assertEquals(RemoteUserMapping.HOST_USER, RemoteUser.prepareRemap(new Docker(), options, CAPTURE, "1000", "1000"));
  }

  @Test
  @DisplayName("falls back to the host user for a numeric user, which usermod cannot take")
  void remapWithNumericUserFallsBackToHostUser() {
    final var options = options(true, "1000", "python:3");
    assertEquals(RemoteUserMapping.HOST_USER, RemoteUser.prepareRemap(new Docker(), options, CAPTURE, "1000", "1000"));
  }

  @Test
  @DisplayName("skips alignment when the host UID/GID cannot be determined")
  void remapWithUnknownHostIdsIsSkipped() {
    final var options = options(true, "node", "python:3");
    assertEquals(RemoteUserMapping.NONE, RemoteUser.prepareRemap(new Docker(), options, CAPTURE, "$(id -u)", "1000"));
  }

  @Test
  @DisplayName("falls back to the host user when the derived image cannot be written")
  void remapWriteFailureFallsBackToHostUser(@TempDir final Path directory, final SystemProperties properties) throws Exception {
    // A regular file as the temp dir makes the write fail on every OS (a missing path would be created
    // silently on Windows).
    final var file = Files.createFile(directory.resolve("not-a-directory"));
    properties.set("java.io.tmpdir", file.toString());
    final var options = options(true, "node", "python:3");
    assertEquals(RemoteUserMapping.HOST_USER, RemoteUser.prepareRemap(new Docker(), options, CAPTURE, "1000", "1000"));
  }

  @Test
  @DisplayName("warns when auto-detecting a user but the image is not available yet")
  void warnsWhenImageUnavailable(final SystemErr systemErr) {
    final var options = options(true, null, "missing:image");
    RemoteUser.resolve(new Podman(), options, args -> "");
    assertEquals(RemoteUserMapping.NONE, options.userMapping);
    assertTrue(systemErr.getText().contains("not available"), systemErr.getText());
  }

  @Test
  @DisplayName("does not warn for a present root image")
  void doesNotWarnForPresentRootImage(final SystemErr systemErr) {
    final var options = options(true, null, "alpine");
    RemoteUser.resolve(new Podman(), options, args -> args.contains("{{.Id}}") ? "sha256:abc" : "");
    assertEquals(RemoteUserMapping.NONE, options.userMapping);
    assertTrue(systemErr.getText().isBlank(), systemErr.getText());
  }

  private static ShellOptions options(final boolean enabled, final String remoteUser, final String image) {
    final var options = new ShellOptions();
    options.updateRemoteUserUID = enabled;
    options.remoteUser = remoteUser;
    options.image = image;
    return options;
  }

  // Options as they look after resolve() has settled on a keep-id mapping, ready for pinKeepId.
  private static ShellOptions keepId(final String remoteUser, final String image) {
    final var options = options(true, remoteUser, image);
    options.userMapping = RemoteUserMapping.KEEP_ID;
    return options;
  }

}
