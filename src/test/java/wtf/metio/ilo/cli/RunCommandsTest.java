/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("RunCommands")
class RunCommandsTest {

  @Test
  @DisplayName("finds .ilo.rc in project root")
  void shouldFindRootJson() {
    assertEquals(1, findRunCommandFiles("root").count());
  }

  @Test
  @DisplayName("finds ilo.rc in .ilo folder")
  void shouldFindNestedJson() {
    assertEquals(1, findRunCommandFiles("nested").count());
  }

  @Test
  @DisplayName("finds multiple .rc files")
  void shouldFindMultiple() {
    assertEquals(2, findRunCommandFiles("multiple").count());
  }

  private Stream<String> findRunCommandFiles(final String testDirectory) {
    final var testResources = Paths.get("src/test/resources/");
    final var testRootDirectory = RunCommands.class.getName().replace(".", "/");
    return RunCommands.locate(testResources.resolve(testRootDirectory).resolve(testDirectory));
  }

}