/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.os;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class ShellTokenizerTest {

  @TestFactory
  Stream<DynamicTest> tokenizeStrings() {
    return Map.ofEntries(
            Map.entry("echo", List.of("echo")),
            Map.entry("mysql -u root -p 'my database'", List.of("mysql", "-u", "root", "-p", "my database")),
            Map.entry("echo foo='bar'", List.of("echo", "foo=bar"))
        )
        .entrySet()
        .stream()
        .map(entry -> DynamicTest.dynamicTest(entry.getKey(), () -> {
          final var tokens = ShellTokenizer.tokenize(entry.getKey());
          assertIterableEquals(entry.getValue(), tokens);
        }));
  }

}
