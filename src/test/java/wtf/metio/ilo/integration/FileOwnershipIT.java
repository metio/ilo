/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import wtf.metio.ilo.Ilo;
import wtf.metio.ilo.cli.Executables;
import wtf.metio.ilo.shell.ShellRuntime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * End-to-end checks of the file-ownership feature against a real container runtime. A non-interactive
 * {@code ilo shell} writes a file into a bind-mounted directory; the file's owner on the host is then
 * the assertion. These run only on Linux (where ownership is observable and the rootful-Docker
 * behavior applies) and only when the runtime is present — required in CI (see {@code requireRuntime}),
 * skipped on a developer machine without it. Named {@code *IT} so they run under the {@code failsafe}
 * plugin in the {@code integration} profile, not in the normal unit-test build.
 */
@DisplayName("file ownership (integration)")
@EnabledOnOs(OS.LINUX)
class FileOwnershipIT {

  private static final String ALPINE = "docker.io/library/alpine:3";

  @Test
  @DisplayName("docker: a file created in a root image is owned by the host user")
  void dockerRootImageKeepsOwnership(@TempDir final Path project) throws IOException {
    requireRuntime("ILO_IT_DOCKER", ShellRuntime.DOCKER);

    final var exit = Ilo.commandLine().execute(
        "shell", "--runtime", "docker", "--no-interactive",
        "--mount-project-dir=false", "--volume", project + ":/work", "--working-dir", "/work",
        "--remove-image",
        ALPINE, "sh", "-c", "touch /work/owned");

    assertEquals(0, exit, "ilo shell exit code");
    assertTrue(Files.exists(project.resolve("owned")), "the container created the file");
    assertEquals(uid(project), uid(project.resolve("owned")), "the file is owned by the host user");
  }

  @Test
  @DisplayName("docker: without the alignment the file is owned by root")
  void dockerWithoutAlignmentIsOwnedByRoot(@TempDir final Path project) throws IOException {
    requireRuntime("ILO_IT_DOCKER", ShellRuntime.DOCKER);

    final var exit = Ilo.commandLine().execute(
        "shell", "--runtime", "docker", "--no-interactive", "--no-update-remote-user-uid",
        "--mount-project-dir=false", "--volume", project + ":/work", "--working-dir", "/work",
        "--remove-image",
        ALPINE, "sh", "-c", "touch /work/owned");

    assertEquals(0, exit, "ilo shell exit code");
    assertEquals(0, uid(project.resolve("owned")), "the file is owned by root without the alignment");
  }

  @Test
  @DisplayName("docker: a non-root image is remapped so files stay owned by the host user")
  void dockerNonRootImageIsRemapped(@TempDir final Path project) throws IOException {
    requireRuntime("ILO_IT_DOCKER", ShellRuntime.DOCKER);

    // A non-root image that ships usermod (debian), built up front so ilo can inspect it and probe for
    // the remap tools; this exercises the derived-image REMAP path rather than the host-user fallback.
    final var tag = "ilo-it-remap:test";
    final var containerfile = Files.writeString(project.resolve("Containerfile"),
        "FROM docker.io/library/debian:stable-slim\nRUN useradd --create-home dev\nUSER dev\n");
    assertEquals(0, Executables.runAndWaitForExit(
            List.of("docker", "build", "--tag", tag, "--file", containerfile.toString(), project.toString()), true),
        "building the non-root base image");

    final var exit = Ilo.commandLine().execute(
        "shell", "--runtime", "docker", "--no-interactive",
        "--mount-project-dir=false", "--volume", project + ":/work", "--working-dir", "/work",
        "--remove-image",
        tag, "sh", "-c", "touch /work/owned");

    assertEquals(0, exit, "ilo shell exit code");
    assertEquals(uid(project), uid(project.resolve("owned")), "the file is remapped to the host user");
  }

  @Test
  @DisplayName("podman: keep-id maps a non-root user so files stay owned by the host user")
  void podmanNonRootImageKeepsOwnership(@TempDir final Path project) throws IOException {
    requireRuntime("ILO_IT_PODMAN", ShellRuntime.PODMAN);

    // A non-root image (alpine needs no usermod for keep-id), built up front so the keep-id UID probe
    // can read the user's IDs from it.
    final var tag = "ilo-it-keepid:test";
    final var containerfile = Files.writeString(project.resolve("Containerfile"),
        "FROM docker.io/library/alpine:3\nRUN adduser -D dev\nUSER dev\n");
    assertEquals(0, Executables.runAndWaitForExit(
            List.of("podman", "build", "--tag", tag, "--file", containerfile.toString(), project.toString()), true),
        "building the non-root base image");

    final var exit = Ilo.commandLine().execute(
        "shell", "--runtime", "podman", "--no-interactive",
        "--mount-project-dir=false", "--volume", project + ":/work", "--working-dir", "/work",
        "--remove-image",
        tag, "sh", "-c", "touch /work/owned");

    assertEquals(0, exit, "ilo shell exit code");
    assertEquals(uid(project), uid(project.resolve("owned")), "the file is kept owned by the host user via keep-id");
  }

  private static int uid(final Path path) throws IOException {
    return (int) Files.getAttribute(path, "unix:uid");
  }

  // Requires the runtime when the CI flag is set, so a misconfigured runner fails loudly rather than
  // silently skipping; on any other machine the runtime's absence simply skips the test.
  private static void requireRuntime(final String ciFlag, final ShellRuntime runtime) {
    final var cli = runtime.cli();
    final var available = cli.exists();
    if ("true".equals(System.getenv(ciFlag))) {
      assertTrue(available, ciFlag + "=true but " + cli.name() + " is not available");
    } else {
      assumeTrue(available, cli.name() + " is not available; skipping");
    }
  }

}
