/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.architecture;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@DisplayName("Dependency Rules")
public final class DependencyRules {

  // The top-level packages form a DAG: none of them may depend on each other in a cycle.
  @ArchTest
  public static final ArchRule freeOfPackageCycles = slices()
      .matching("wtf.metio.ilo.(*)..")
      .should().beFreeOfCycles();

  // Foundational packages sit at the bottom of the layering: they may use the JDK and third-party
  // libraries (and their own package), but must not depend on any other ilo package.
  @ArchTest
  public static final ArchRule errorsAreFoundational = foundational("errors");

  @ArchTest
  public static final ArchRule utilsAreFoundational = foundational("utils");

  @ArchTest
  public static final ArchRule versionIsFoundational = foundational("version");

  private static ArchRule foundational(final String name) {
    final var self = "wtf.metio.ilo." + name + "..";
    return classes()
        .that().resideInAPackage(self)
        .should().onlyDependOnClassesThat(resideInAPackage(self).or(resideOutsideOfPackage("wtf.metio.ilo..")))
        .as("classes in " + name + " should not depend on other ilo packages");
  }

}
