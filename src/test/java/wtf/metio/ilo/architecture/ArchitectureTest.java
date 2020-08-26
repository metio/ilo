/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.*;
import wtf.metio.ilo.Ilo;
import wtf.metio.ilo.test.ArchUnitTests;

import java.util.stream.Stream;

@DisplayName("Architecture")
public final class ArchitectureTest {

  private static JavaClasses classes;

  @BeforeAll
  static void importPackages() {
    classes = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages(Ilo.class.getPackageName());
  }

  @TestFactory
  @DisplayName("Global Rules")
  Stream<DynamicNode> globalRules() {
    return Stream.of(CodingRules.class, StructureRules.class, LayerRules.class)
        .map(clazz -> ArchUnitTests.in(clazz, rule -> rule.check(classes)));
  }

  @TestFactory
  @DisplayName("Implementation Rules")
  Stream<DynamicNode> implementationRules() {
    return Stream.of(CliRules.class, ErrorsRules.class, ToolsRules.class)
        .map(clazz -> ArchUnitTests.in(clazz, rule -> rule.check(classes)));
  }

  @Test
  @DisplayName("Setup Works")
  @Disabled("figure out why ArchUnit cannot find classes")
  void printClasses() {
    Assertions.assertTrue(classes.contain("wtf.metio.ilo.Ilo"));
  }

}
