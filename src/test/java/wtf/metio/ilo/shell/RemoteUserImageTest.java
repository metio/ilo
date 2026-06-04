/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;
import wtf.metio.ilo.errors.RuntimeIOException;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RemoteUserImage")
class RemoteUserImageTest {

  @Nested
  @DisplayName("imageUser")
  class ImageUser {

    @Test
    @DisplayName("reads the user name from an inspect result")
    void name() {
      assertEquals("vscode", RemoteUserImage.imageUser("vscode\n"));
    }

    @Test
    @DisplayName("keeps only the user part of a user:group spec")
    void userAndGroup() {
      assertEquals("vscode", RemoteUserImage.imageUser("vscode:devs"));
    }

    @Test
    @DisplayName("returns null for an empty result")
    void empty() {
      assertNull(RemoteUserImage.imageUser("\n"));
    }

    @Test
    @DisplayName("returns null for a null result")
    void nullResult() {
      assertNull(RemoteUserImage.imageUser(null));
    }

    @Test
    @DisplayName("returns null when only a group is given")
    void groupOnly() {
      assertNull(RemoteUserImage.imageUser(":devs"));
    }
  }

  @Nested
  @DisplayName("containerfile")
  class Containerfile {

    @Test
    @DisplayName("builds FROM the image for an image-based environment")
    void fromImage() {
      final var content = RemoteUserImage.containerfile("python:3", null, "vscode", "1000", "1000");
      assertAll("generated containerfile",
          () -> assertTrue(content.startsWith("FROM python:3\n"), content),
          () -> assertTrue(content.contains("USER root\n"), content),
          () -> assertTrue(content.contains("usermod -o -u 1000 -g 1000 vscode"), content),
          () -> assertTrue(content.contains("id -gn vscode"), content),
          () -> assertTrue(content.endsWith("USER vscode\n"), content));
    }

    @Test
    @DisplayName("layers the remap on top of an existing containerfile")
    void fromContainerfile() {
      final var base = "FROM debian\nRUN apt-get update\n";
      final var content = RemoteUserImage.containerfile(null, base, "node", "501", "20");
      assertAll("layered containerfile",
          () -> assertTrue(content.startsWith(base), content),
          () -> assertTrue(content.contains("usermod -o -u 501 -g 20 node"), content),
          () -> assertTrue(content.endsWith("USER node\n"), content));
    }
  }

  @Nested
  @DisplayName("tag")
  class Tag {

    @Test
    @DisplayName("derives a stable repository tag from the content")
    void stable() {
      final var content = RemoteUserImage.containerfile("python:3", null, "vscode", "1000", "1000");
      assertEquals(RemoteUserImage.tag(content), RemoteUserImage.tag(content));
      assertTrue(RemoteUserImage.tag(content).startsWith("ilo-remote-user:"), RemoteUserImage.tag(content));
    }

    @Test
    @DisplayName("differs for different content")
    void distinct() {
      final var one = RemoteUserImage.containerfile("python:3", null, "vscode", "1000", "1000");
      final var two = RemoteUserImage.containerfile("python:3", null, "vscode", "1001", "1001");
      assertTrue(!RemoteUserImage.tag(one).equals(RemoteUserImage.tag(two)));
    }
  }

  @Nested
  @DisplayName("rewrite")
  class Rewrite {

    @Test
    @DisplayName("points an image-based environment at a generated containerfile and derived tag")
    void imageBased() throws Exception {
      final var options = new ShellOptions();
      options.image = "python:3";
      options.remoteUser = "vscode";

      RemoteUserImage.rewrite(options, "1000", "1000");

      assertAll("rewritten options",
          () -> assertTrue(options.image.startsWith("ilo-remote-user:"), options.image),
          () -> assertTrue(options.containerfile != null && Files.exists(Path.of(options.containerfile))),
          () -> assertTrue(Files.readString(Path.of(options.containerfile)).startsWith("FROM python:3\n")),
          () -> assertEquals(Path.of(options.containerfile).getParent().toString(), options.context));
    }

    @Test
    @DisplayName("layers onto an existing containerfile and keeps its build context")
    void containerfileBased() throws Exception {
      final var base = Files.createTempFile("ilo-remote-user-base-", ".containerfile");
      Files.writeString(base, "FROM debian\n");
      final var options = new ShellOptions();
      options.image = "local/dev:latest";
      options.containerfile = base.toString();
      options.context = "/project";
      options.remoteUser = "node";

      RemoteUserImage.rewrite(options, "501", "20");

      assertAll("layered options",
          () -> assertTrue(options.image.startsWith("ilo-remote-user:"), options.image),
          () -> assertTrue(Files.readString(Path.of(options.containerfile)).startsWith("FROM debian\n")),
          () -> assertTrue(Files.readString(Path.of(options.containerfile)).contains("usermod -o -u 501 -g 20 node")),
          () -> assertEquals("/project", options.context, "keeps the original build context"));
      Files.deleteIfExists(base);
    }
  }

  @Nested
  @DisplayName("failures")
  @ExtendWith(SystemStubsExtension.class)
  class Failures {

    @Test
    @DisplayName("reports an unreadable existing containerfile")
    void unreadableContainerfile() {
      final var options = new ShellOptions();
      options.image = "local/dev:latest";
      options.containerfile = "/does/not/exist/Containerfile";
      options.remoteUser = "node";
      assertThrows(RuntimeIOException.class, () -> RemoteUserImage.rewrite(options, "1000", "1000"));
    }

    @Test
    @DisplayName("reports a generated containerfile that cannot be written")
    void unwritableTempDirectory(final SystemProperties properties) throws Exception {
      properties.set("java.io.tmpdir", "/does/not/exist");
      final var options = new ShellOptions();
      options.image = "python:3";
      options.remoteUser = "node";
      assertThrows(RuntimeIOException.class, () -> RemoteUserImage.rewrite(options, "1000", "1000"));
    }
  }

}
