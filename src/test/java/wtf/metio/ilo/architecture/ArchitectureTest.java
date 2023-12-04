/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
        .importPackagesOf(Ilo.class);
  }

  @TestFactory
  @DisplayName("Global Rules")
  Stream<DynamicNode> globalRules() {
    return Stream.of(CodingRules.class, StructureRules.class, LayerRules.class)
        .map(clazz -> ArchUnitTests.in(clazz, rule -> rule.check(classes)));
  }

  @TestFactory
  @DisplayName("Implementation Rules")
  @Disabled
  Stream<DynamicNode> implementationRules() {
    return Stream.of();
    //return Stream.of(CliRules.class, ErrorsRules.class, ToolsRules.class)
    //  .map(clazz -> ArchUnitTests.in(clazz, rule -> rule.check(classes)));
  }

}
