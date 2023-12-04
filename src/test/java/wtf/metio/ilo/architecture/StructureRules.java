/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.architecture;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@DisplayName("Structural Rules")
public final class StructureRules {

  @ArchTest
  public static final ArchRule iloDependsOnCommands = classes()
      .that().haveFullyQualifiedName("wtf.metio.ilo.Ilo")
      .should().dependOnClassesThat().resideInAnyPackage("..shell..");

}
