/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.cli;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
  @DisplayName("allow to disable run commands")
  void turnOffRunCommands() {
    assertFalse(RunCommands.shouldAddRunCommands(new String[]{"--no-rc"}));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("allow to configure different run command file")
  void configureRunCommandFile() throws Exception {
    SystemLambda.withEnvironmentVariable(EnvironmentVariables.ILO_RC.name(), "some-name.rc")
      .execute(() -> assertEquals(1, findRunCommandFiles("different").count()));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("allow to configure different run command files")
  void configureRunCommandFiles() throws Exception {
    SystemLambda.withEnvironmentVariable(EnvironmentVariables.ILO_RC.name(), "some-name.rc,another.rc")
      .execute(() -> assertEquals(2, findRunCommandFiles("different").count()));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("allow to configure different run command files where one is missing")
  void configureRunCommandFilesWithMissing() throws Exception {
    SystemLambda.withEnvironmentVariable(EnvironmentVariables.ILO_RC.name(), "missing.rc,some-name.rc")
      .execute(() -> assertEquals(1, findRunCommandFiles("different").count()));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("handle missing run command file")
  void configureMissingRunCommandFile() throws Exception {
    SystemLambda.withEnvironmentVariable(EnvironmentVariables.ILO_RC.name(), "missing.rc")
      .execute(() -> assertEquals(0, findRunCommandFiles("different").count()));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("handle missing run command files")
  void configureMissingRunCommandFiles() throws Exception {
    SystemLambda.withEnvironmentVariable(EnvironmentVariables.ILO_RC.name(), "missing.rc,deleted.rc")
      .execute(() -> assertEquals(0, findRunCommandFiles("different").count()));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  @DisplayName("allow to configure run command files with whitespace after the comma")
  void configureRunCommandFilesWithWhitespace() throws Exception {
    SystemLambda.withEnvironmentVariable(EnvironmentVariables.ILO_RC.name(), "missing.rc, some-name.rc")
      .execute(() -> assertEquals(1, findRunCommandFiles("different").count()));
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
