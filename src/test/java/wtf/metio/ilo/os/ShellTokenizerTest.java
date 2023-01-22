/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
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
