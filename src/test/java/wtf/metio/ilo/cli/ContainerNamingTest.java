/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ContainerNaming")
class ContainerNamingTest {

  @Test
  @DisplayName("is deterministic for the same inputs")
  void shouldBeDeterministic() {
    assertEquals(
        ContainerNaming.containerName("ilo-shell", "/home/project", "alpine:3"),
        ContainerNaming.containerName("ilo-shell", "/home/project", "alpine:3"));
  }

  @Test
  @DisplayName("differs when the project directory differs")
  void shouldDifferByProject() {
    assertNotEquals(
        ContainerNaming.containerName("ilo-shell", "/home/a", "alpine:3"),
        ContainerNaming.containerName("ilo-shell", "/home/b", "alpine:3"));
  }

  @Test
  @DisplayName("differs when the image differs")
  void shouldDifferByImage() {
    assertNotEquals(
        ContainerNaming.containerName("ilo-shell", "/home/project", "alpine:3"),
        ContainerNaming.containerName("ilo-shell", "/home/project", "ubuntu:24.04"));
  }

  @Test
  @DisplayName("starts with the given prefix and a separator")
  void shouldStartWithPrefix() {
    assertTrue(ContainerNaming.containerName("ilo-shell", "/home/project", "alpine:3").startsWith("ilo-shell-"));
  }

  @Test
  @DisplayName("appends a fixed-length lower-case hex digest")
  void shouldAppendHexDigest() {
    final var name = ContainerNaming.containerName("ilo-shell", "/home/project", "alpine:3");
    final var digest = name.substring("ilo-shell-".length());
    assertTrue(digest.matches("[0-9a-f]{12}"), () -> "unexpected digest: " + digest);
  }

  @Test
  @DisplayName("produces a name valid for container runtimes")
  void shouldProduceValidName() {
    final var name = ContainerNaming.containerName("ilo-devcontainer", "/home/project", "");
    assertTrue(name.matches("[a-zA-Z0-9][a-zA-Z0-9_.-]+"), () -> "invalid container name: " + name);
  }

}
