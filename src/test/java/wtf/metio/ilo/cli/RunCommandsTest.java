/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static wtf.metio.ilo.test.TestResources.testResources;

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

  @Test
  @DisplayName("finds none in empty directory")
  void shouldFindNoneInEmptyDirectory() {
    assertEquals(0, findRunCommandFiles("empty").count());
  }

  @Test
  @DisplayName("ignores directories called .ilo.rc")
  void shouldIgnoreDirectories() {
    assertEquals(0, findRunCommandFiles("directory").count());
  }

  private Stream<String> findRunCommandFiles(final String testDirectory) {
    return RunCommands.locate(testResources(RunCommands.class).resolve(testDirectory));
  }

  @Test
  @DisplayName("allow to specify --help")
  void noRunCommandsForHelp() {
    assertFalse(RunCommands.shouldAddRunCommands(new String[]{"--help"}));
  }

  @Test
  @DisplayName("allow to specify -h")
  void noRunCommandsForShortHelp() {
    assertFalse(RunCommands.shouldAddRunCommands(new String[]{"-h"}));
  }

  @Test
  @DisplayName("allow to specify --help for a command")
  void noRunCommandsForHelpOfCommand() {
    assertFalse(RunCommands.shouldAddRunCommands(new String[]{"shell", "--help"}));
  }

  @Test
  @DisplayName("allow to specify -h for a command")
  void noRunCommandsForShortHelpOfCommand() {
    assertFalse(RunCommands.shouldAddRunCommands(new String[]{"shell", "-h"}));
  }

  @Test
  @DisplayName("allow to specify --version")
  void noRunCommandsForVersion() {
    assertFalse(RunCommands.shouldAddRunCommands(new String[]{"--version"}));
  }

  @Test
  @DisplayName("allow to specify -V")
  void noRunCommandsForShortVersion() {
    assertFalse(RunCommands.shouldAddRunCommands(new String[]{"-V"}));
  }

  @Test
  @DisplayName("allow to specify --version for a command")
  void noRunCommandsForVersionOfCommand() {
    assertFalse(RunCommands.shouldAddRunCommands(new String[]{"shell", "--version"}));
  }

  @Test
  @DisplayName("allow to specify -V for a command")
  void noRunCommandsForShortVersionOfCommand() {
    assertFalse(RunCommands.shouldAddRunCommands(new String[]{"shell", "-V"}));
  }

  @Test
  @DisplayName("allow to call generate-completion without run commands")
  void noRunCommandsForGenerateCompletion() {
    assertFalse(RunCommands.shouldAddRunCommands(new String[]{"generate-completion"}));
  }

  @Test
  @DisplayName("allow to call ilo with run commands")
  void runCommandsForMainCommand() {
    assertTrue(RunCommands.shouldAddRunCommands(new String[]{}));
  }

  @Test
  @DisplayName("allow to call 'ilo <command>' with run commands")
  void runCommandsForSubCommand() {
    assertTrue(RunCommands.shouldAddRunCommands(new String[]{"shell"}));
    assertTrue(RunCommands.shouldAddRunCommands(new String[]{"compose"}));
    assertTrue(RunCommands.shouldAddRunCommands(new String[]{"devcontainer"}));
  }

}