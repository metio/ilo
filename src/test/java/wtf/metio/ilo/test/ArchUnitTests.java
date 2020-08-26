/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.test;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.*;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public final class ArchUnitTests {

  private ArchUnitTests() {
    // helper class
  }

  public static DynamicNode in(final Class<?> clazz, final Consumer<? super ArchRule> check) {
    final var displayName = clazz.getAnnotation(DisplayName.class);
    return dynamicContainer(displayName.value(), in(clazz)
        .map(rule -> dynamicTest(rule.getDescription(), () -> check.accept(rule))));
  }

  public static Stream<ArchRule> in(final Class<?> clazz) {
    return Arrays.stream(clazz.getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(ArchTest.class))
        .filter(field -> ArchRule.class.isAssignableFrom(field.getType()))
        .filter(field -> isPublic(field.getModifiers()))
        .filter(field -> isStatic(field.getModifiers()))
        .filter(field -> isFinal(field.getModifiers()))
        .map(ArchUnitTests::value);
  }

  static ArchRule value(final Field field) {
    try {
      return (ArchRule) field.get(null);
    } catch (final IllegalAccessException exception) {
      throw new RuntimeException(exception);
    }
  }

}
