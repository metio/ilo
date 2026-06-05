/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ShellExpansion")
class ShellExpansionTest {

  // A ShellExpansion whose primitives are deterministic markers, so the single-scan logic can be
  // verified without a real shell. It records every body/reference it is asked to expand, which is how
  // the tests prove a substitution's output is never fed back through the scanner.
  private static final class StubShell extends ShellExpansion {

    private final boolean backticks;
    private final UnaryOperator<String> command;
    private final UnaryOperator<String> parameter;
    final List<String> commands = new ArrayList<>();
    final List<String> parameters = new ArrayList<>();

    private StubShell(final boolean backticks, final UnaryOperator<String> command, final UnaryOperator<String> parameter) {
      this.backticks = backticks;
      this.command = command;
      this.parameter = parameter;
    }

    static StubShell marking() {
      return new StubShell(true, body -> "[c:" + body + "]", reference -> "[p:" + reference + "]");
    }

    @Override
    String commandOutput(final String script) {
      commands.add(script);
      return command.apply(script);
    }

    @Override
    String parameterValue(final String reference) {
      parameters.add(reference);
      return parameter.apply(reference);
    }

    @Override
    boolean backticksAreCommands() {
      return backticks;
    }
  }

  @Nested
  @DisplayName("command substitution")
  class Commands {

    @Test
    @DisplayName("substitutes a single command")
    void single() {
      final var shell = StubShell.marking();
      assertEquals("[c:some --opt]", shell.expand("$(some --opt)"));
      assertIterableEquals(List.of("some --opt"), shell.commands);
    }

    @Test
    @DisplayName("substitutes multiple commands and keeps the surrounding text")
    void multiple() {
      final var shell = StubShell.marking();
      assertEquals("a[c:one]b[c:two]c", shell.expand("a$(one)b$(two)c"));
      assertIterableEquals(List.of("one", "two"), shell.commands);
    }

    @Test
    @DisplayName("hands a nested command to the shell as one body")
    void nested() {
      final var shell = StubShell.marking();
      assertEquals("[c:echo $(inner)]", shell.expand("$(echo $(inner))"));
      assertIterableEquals(List.of("echo $(inner)"), shell.commands);
    }

    @Test
    @DisplayName("leaves an unbalanced command substitution untouched")
    void unbalanced() {
      final var shell = StubShell.marking();
      assertEquals("x:$(echo unclosed", shell.expand("x:$(echo unclosed"));
      assertTrue(shell.commands.isEmpty(), shell.commands.toString());
    }

    @Test
    @DisplayName("substitutes a backtick command when the shell supports it")
    void backtick() {
      final var shell = StubShell.marking();
      assertEquals("[c:id -u]", shell.expand("`id -u`"));
      assertIterableEquals(List.of("id -u"), shell.commands);
    }

    @Test
    @DisplayName("leaves a backtick literal when the shell does not treat it as a command")
    void backtickDisabled() {
      final var shell = new StubShell(false, body -> "[c:" + body + "]", reference -> "[p:" + reference + "]");
      assertEquals("`id -u`", shell.expand("`id -u`"));
      assertTrue(shell.commands.isEmpty(), shell.commands.toString());
    }

    @Test
    @DisplayName("leaves an unbalanced backtick untouched")
    void unbalancedBacktick() {
      final var shell = StubShell.marking();
      assertEquals("a`id -u", shell.expand("a`id -u"));
      assertTrue(shell.commands.isEmpty(), shell.commands.toString());
    }
  }

  @Nested
  @DisplayName("parameter expansion")
  class Parameters {

    @Test
    @DisplayName("expands a bare parameter")
    void bare() {
      final var shell = StubShell.marking();
      assertEquals("[p:$HOME]:tail", shell.expand("$HOME:tail"));
      assertIterableEquals(List.of("$HOME"), shell.parameters);
    }

    @Test
    @DisplayName("normalizes a braced parameter to its bare reference")
    void braced() {
      final var shell = StubShell.marking();
      assertEquals("[p:$HOME]b", shell.expand("${HOME}b"));
      assertIterableEquals(List.of("$HOME"), shell.parameters);
    }

    @Test
    @DisplayName("expands a parameter whose name contains digits and underscores")
    void nameWithDigitsAndUnderscores() {
      final var shell = StubShell.marking();
      assertEquals("[p:$a_1]", shell.expand("$a_1"));
      assertIterableEquals(List.of("$a_1"), shell.parameters);
    }

    @Test
    @DisplayName("leaves a dollar that does not introduce a name literal")
    void dollarWithoutName() {
      final var shell = StubShell.marking();
      assertEquals("$5 and $", shell.expand("$5 and $"));
      assertTrue(shell.parameters.isEmpty(), shell.parameters.toString());
    }

    @Test
    @DisplayName("leaves an unterminated braced reference literal")
    void bracedUnterminated() {
      final var shell = StubShell.marking();
      assertEquals("${HOME", shell.expand("${HOME"));
      assertTrue(shell.parameters.isEmpty(), shell.parameters.toString());
    }

    @Test
    @DisplayName("leaves an empty braced reference literal")
    void bracedEmpty() {
      final var shell = StubShell.marking();
      assertEquals("${}", shell.expand("${}"));
      assertTrue(shell.parameters.isEmpty(), shell.parameters.toString());
    }

    @Test
    @DisplayName("leaves a braced reference whose name starts with a digit literal")
    void bracedInvalidStart() {
      final var shell = StubShell.marking();
      assertEquals("${1bad}", shell.expand("${1bad}"));
      assertTrue(shell.parameters.isEmpty(), shell.parameters.toString());
    }

    @Test
    @DisplayName("leaves a braced reference containing an invalid character literal")
    void bracedInvalidChar() {
      final var shell = StubShell.marking();
      assertEquals("${a-b}", shell.expand("${a-b}"));
      assertTrue(shell.parameters.isEmpty(), shell.parameters.toString());
    }

    @Test
    @DisplayName("keeps a value with no expandable construct unchanged")
    void constant() {
      final var shell = StubShell.marking();
      assertEquals("1000:1000", shell.expand("1000:1000"));
      assertTrue(shell.commands.isEmpty() && shell.parameters.isEmpty());
    }
  }

  @Nested
  @DisplayName("single pass — output is never re-scanned")
  class SinglePass {

    @Test
    @DisplayName("a parameter value that contains a command substitution is not executed")
    void parameterValueIsNotReScannedForCommands() {
      // The parameter's value is '$(evil)'; a shell never re-evaluates a variable's value for command
      // substitution, so it must appear verbatim and no command may be run.
      final var shell = new StubShell(true, body -> "RAN:" + body, reference -> "$(evil)");
      assertEquals("$(evil)", shell.expand("$FOO"));
      assertTrue(shell.commands.isEmpty(), shell.commands.toString());
    }

    @Test
    @DisplayName("a command's output that contains backticks is not re-executed")
    void commandOutputIsNotReScannedForBackticks() {
      final var shell = new StubShell(true, body -> "a`echo X`b", reference -> "[p:" + reference + "]");
      assertEquals("a`echo X`b", shell.expand("$(producer)"));
      assertIterableEquals(List.of("producer"), shell.commands);
    }

    @Test
    @DisplayName("a command's output that contains a parameter reference is not re-expanded")
    void commandOutputIsNotReScannedForParameters() {
      final var shell = new StubShell(true, body -> "$HOME", reference -> "RE:" + reference);
      assertEquals("$HOME", shell.expand("$(pwd)"));
      assertTrue(shell.parameters.isEmpty(), shell.parameters.toString());
    }

    @Test
    @DisplayName("output containing a dollar or backslash is appended verbatim")
    void outputWithDollarOrBackslashIsVerbatim() {
      final var shell = new StubShell(true, body -> "$5 \\ end", reference -> "[p:" + reference + "]");
      assertEquals("$5 \\ end", shell.expand("$(x)"));
    }
  }

  @Nested
  @DisplayName("tilde expansion")
  @ExtendWith(SystemStubsExtension.class)
  class TildeExpansion {

    private final ShellExpansion shell = StubShell.marking();

    @Test
    @DisplayName("expands a leading tilde followed by a slash")
    void leadingWithSlash(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("/home/user/work", shell.expandTilde("~/work"));
    }

    @Test
    @DisplayName("expands a bare tilde")
    void bare(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("/home/user", shell.expandTilde("~"));
    }

    @Test
    @DisplayName("expands a tilde after a colon separator")
    void afterColon(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("/data:/home/user/cache", shell.expandTilde("/data:~/cache"));
    }

    @Test
    @DisplayName("expands a tilde after an equals separator")
    void afterEquals(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("DIR=/home/user/x", shell.expandTilde("DIR=~/x"));
    }

    @Test
    @DisplayName("does not expand a tilde inside a path component")
    void insidePath(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("/data/backup~2:/mnt", shell.expandTilde("/data/backup~2:/mnt"));
    }

    @Test
    @DisplayName("does not expand a tilde that introduces another user's name")
    void otherUser(final SystemProperties properties) {
      properties.set("user.home", "/home/user");
      assertEquals("~bob/work", shell.expandTilde("~bob/work"));
    }

    @Test
    @DisplayName("keeps a backslash in the home directory literal")
    void backslashInHome(final SystemProperties properties) {
      properties.set("user.home", "C:\\Users\\me");
      assertEquals("C:\\Users\\me/work", shell.expandTilde("~/work"));
    }
  }

}
