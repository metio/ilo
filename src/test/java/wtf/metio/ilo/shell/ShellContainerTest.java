/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ShellContainer")
class ShellContainerTest {

  private static final String PROJECT = "/home/user/my-project";

  private ShellOptions options() {
    final var options = new ShellOptions();
    options.image = "alpine:3";
    return options;
  }

  @ParameterizedTest
  @DisplayName("derives a readable slug from the project directory")
  @CsvSource({
      "/home/user/my-project, my-project",
      "/srv/App_1.2, app_1.2",
      "/x/My Proj!, my-proj",
      "/, project"
  })
  void shouldDeriveSlug(final String projectDir, final String expected) {
    assertEquals(expected, ShellContainer.slug(projectDir));
  }

  @Test
  @DisplayName("names the container ilo-<slug>-<hash>")
  void shouldName() {
    final var name = ShellContainer.name(options(), PROJECT);
    assertTrue(name.matches("ilo-my-project-[0-9a-f]{12}"), () -> "unexpected name: " + name);
  }

  @Test
  @DisplayName("fingerprint is stable for the same definition")
  void shouldBeStable() {
    assertEquals(ShellContainer.fingerprint(options(), PROJECT), ShellContainer.fingerprint(options(), PROJECT));
  }

  @Test
  @DisplayName("fingerprint changes with the image")
  void shouldChangeWithImage() {
    final var other = options();
    other.image = "ubuntu:24.04";
    assertNotEquals(ShellContainer.fingerprint(options(), PROJECT), ShellContainer.fingerprint(other, PROJECT));
  }

  @Test
  @DisplayName("fingerprint changes with the run configuration")
  void shouldChangeWithRunConfig() {
    final var other = options();
    other.variables = List.of("KEY=value");
    assertNotEquals(ShellContainer.fingerprint(options(), PROJECT), ShellContainer.fingerprint(other, PROJECT));
  }

  @Test
  @DisplayName("fingerprint changes with the extra identity source")
  void shouldChangeWithIdentitySource() {
    final var other = options();
    other.identitySource = "postCreateCommand: npm install";
    assertNotEquals(ShellContainer.fingerprint(options(), PROJECT), ShellContainer.fingerprint(other, PROJECT));
  }

  @Test
  @DisplayName("fingerprint changes when the Containerfile contents change")
  void shouldChangeWithContainerfileContents(@TempDir final Path directory) throws IOException {
    final var containerfile = directory.resolve("Containerfile");
    Files.writeString(containerfile, "FROM alpine:3\n");
    final var options = options();
    options.containerfile = containerfile.toString();
    final var before = ShellContainer.fingerprint(options, PROJECT);

    Files.writeString(containerfile, "FROM alpine:3\nRUN echo changed\n");
    final var after = ShellContainer.fingerprint(options, PROJECT);

    assertNotEquals(before, after);
  }

}
