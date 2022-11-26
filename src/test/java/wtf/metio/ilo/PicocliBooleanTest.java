/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
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

    @Override
    public void run() {}

  }

  @Test
  public void optionNotSpecified() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs();
    assertAll(
        () -> assertTrue(command.interactive, "interactive"),
        () -> assertFalse(command.autonomous, "autonomous"),
        () -> assertTrue(command.daemon, "daemon"),
        () -> assertFalse(command.human, "human")
    );
  }

  @Test
  public void optionSpecifiedWithoutValue() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs("--interactive", "--autonomous", "--daemon", "--human");
    assertAll(
        () -> assertTrue(command.interactive, "interactive"),
        () -> assertFalse(command.autonomous, "autonomous"),
        () -> assertFalse(command.daemon, "daemon"),
        () -> assertTrue(command.human, "human")
    );
  }

  @Test
  public void optionSpecifiedWithBooleanTrue() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs("--interactive=true", "--autonomous=true", "--daemon=true", "--human=true");
    assertAll(
        () -> assertTrue(command.interactive, "interactive"),
        () -> assertTrue(command.autonomous, "autonomous"),
        () -> assertTrue(command.daemon, "daemon"),
        () -> assertTrue(command.human, "human")
    );
  }

  @Test
  public void optionSpecifiedWithBooleanFalse() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs("--interactive=false", "--autonomous=false", "--daemon=false", "--human=false");
    assertAll(
        () -> assertFalse(command.interactive, "interactive"),
        () -> assertFalse(command.autonomous, "autonomous"),
        () -> assertFalse(command.daemon, "daemon"),
        () -> assertFalse(command.human, "human")
    );
  }

  @Test
  public void optionSpecifiedWithNegatedForm() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs("--no-interactive", "--no-autonomous", "--no-daemon", "--no-human");
    assertAll(
        () -> assertFalse(command.interactive, "interactive"),
        () -> assertTrue(command.autonomous, "autonomous"),
        () -> assertTrue(command.daemon, "daemon"),
        () -> assertFalse(command.human, "human")
    );
  }

  @Test
  public void optionSpecifiedWithNegatedFormUsingBooleanTrue() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs("--no-interactive=true", "--no-autonomous=true", "--no-daemon=true", "--no-human=true");
    assertAll(
        () -> assertTrue(command.interactive, "interactive"),
        () -> assertTrue(command.autonomous, "autonomous"),
        () -> assertTrue(command.daemon, "daemon"),
        () -> assertTrue(command.human, "human")
    );
  }

  @Test
  public void optionSpecifiedWithNegatedFormUsingBooleanFalse() {
    final var command = new TestCommand();
    new CommandLine(command).parseArgs("--no-interactive=false", "--no-autonomous=false", "--no-daemon=false", "--no-human=false");
    assertAll(
        () -> assertFalse(command.interactive, "interactive"),
        () -> assertFalse(command.autonomous, "autonomous"),
        () -> assertFalse(command.daemon, "daemon"),
        () -> assertFalse(command.human, "human")
    );
  }

}
