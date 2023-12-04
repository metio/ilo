/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;

public class PicocliBooleanTest {

  @CommandLine.Command
  static class TestCommand implements Runnable {

    @CommandLine.Option(
        names = {"--no-interactive"},
        defaultValue = "true",
        negatable = true
    )
    public boolean interactive;

    @CommandLine.Option(
        names = {"--no-autonomous"},
        defaultValue = "false",
        negatable = true
    )
    public boolean autonomous;

    @CommandLine.Option(
        names = {"--daemon"},
        defaultValue = "true",
        negatable = true
    )
    public boolean daemon;

    @CommandLine.Option(
        names = {"--human"},
        defaultValue = "false",
        negatable = true
    )
    public boolean human;

    @CommandLine.Option(
        names = {"--wanted"},
        defaultValue = "true",
        fallbackValue = "true",
        negatable = true
    )
    public boolean wanted;

    @Override
    public void run() {
    }

  }

  @Test
  public void optionNotSpecified() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs();
    assertAll(
        () -> assertTrue(command.interactive, "interactive"),
        () -> assertFalse(command.autonomous, "autonomous"),
        () -> assertTrue(command.daemon, "daemon"),
        () -> assertFalse(command.human, "human"),
        () -> assertTrue(command.wanted, "wanted")
    );
  }

  @Test
  public void optionSpecifiedWithoutValue() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs("--interactive", "--autonomous", "--daemon", "--human", "--wanted");
    assertAll(
        () -> assertTrue(command.interactive, "interactive"),
        () -> assertFalse(command.autonomous, "autonomous"),
        () -> assertFalse(command.daemon, "daemon"),
        () -> assertTrue(command.human, "human"),
        () -> assertTrue(command.wanted, "wanted")
    );
  }

  @Test
  public void optionSpecifiedWithBooleanTrue() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs("--interactive=true", "--autonomous=true", "--daemon=true", "--human=true", "--wanted=true");
    assertAll(
        () -> assertFalse(command.interactive, "interactive"),
        () -> assertFalse(command.autonomous, "autonomous"),
        () -> assertTrue(command.daemon, "daemon"),
        () -> assertTrue(command.human, "human"),
        () -> assertTrue(command.wanted, "wanted")
    );
  }

  @Test
  public void optionSpecifiedWithBooleanFalse() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs("--interactive=false", "--autonomous=false", "--daemon=false", "--human=false", "--wanted=false");
    assertAll(
        () -> assertTrue(command.interactive, "interactive"),
        () -> assertTrue(command.autonomous, "autonomous"),
        () -> assertFalse(command.daemon, "daemon"),
        () -> assertFalse(command.human, "human"),
        () -> assertFalse(command.wanted, "wanted")
    );
  }

  @Test
  public void optionSpecifiedWithNegatedForm() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs("--no-interactive", "--no-autonomous", "--no-daemon", "--no-human", "--no-wanted");
    assertAll(
        () -> assertFalse(command.interactive, "interactive"),
        () -> assertTrue(command.autonomous, "autonomous"),
        () -> assertTrue(command.daemon, "daemon"),
        () -> assertFalse(command.human, "human"),
        () -> assertFalse(command.wanted, "wanted")
    );
  }

  @Test
  public void optionSpecifiedWithNegatedFormUsingBooleanTrue() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs("--no-interactive=true", "--no-autonomous=true", "--no-daemon=true", "--no-human=true", "--no-wanted=true");
    assertAll(
        () -> assertTrue(command.interactive, "interactive"),
        () -> assertTrue(command.autonomous, "autonomous"),
        () -> assertFalse(command.daemon, "daemon"),
        () -> assertFalse(command.human, "human"),
        () -> assertFalse(command.wanted, "wanted")
    );
  }

  @Test
  public void optionSpecifiedWithNegatedFormUsingBooleanFalse() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs("--no-interactive=false", "--no-autonomous=false", "--no-daemon=false", "--no-human=false", "--no-wanted=false");
    assertAll(
        () -> assertFalse(command.interactive, "interactive"),
        () -> assertFalse(command.autonomous, "autonomous"),
        () -> assertTrue(command.daemon, "daemon"),
        () -> assertTrue(command.human, "human"),
        () -> assertTrue(command.wanted, "wanted")
    );
  }

}
