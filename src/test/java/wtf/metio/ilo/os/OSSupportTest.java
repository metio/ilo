/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.os;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("OSSupport")
class OSSupportTest {

  @Test
  @DisplayName("detects the host shell once per expander, reused across every expansion")
  void detectsShellOncePerExpander() {
    final var detections = new AtomicInteger();
    final Supplier<ParameterExpansion> detector = () -> {
      detections.incrementAndGet();
      return new NoOpExpansion();
    };

    final var expand = OSSupport.expander(detector);
    expand.expand("a");
    expand.expand(List.of("b", "c", "d"));
    expand.expand("e");

    assertEquals(1, detections.get(), "the shell should be detected once, not per value");
  }

  @Test
  @DisplayName("applies the expansion to every value and drops blanks")
  void appliesExpansionToEachValue() {
    final var expand = new OSSupport.Expander(new ParameterExpansion() {
      @Override
      String expand(final String value) {
        return value.toUpperCase(Locale.ROOT);
      }
    });

    assertEquals(List.of("A", "B"), expand.expand(List.of("a", "", "  ", "b")));
  }

  @Test
  @DisplayName("delegates each value to the shell expansion")
  void delegatesToExpansion() {
    final var expand = new OSSupport.Expander(new ParameterExpansion() {
      @Override
      String expand(final String value) {
        return value + ":expanded";
      }
    });

    assertEquals("v:expanded", expand.expand("v"));
  }

  @Test
  @DisplayName("keeps a null value")
  void keepsNullValue() {
    assertNull(new OSSupport.Expander(new NoOpExpansion()).expand((String) null));
  }

}
