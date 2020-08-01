/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.architecture;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@DisplayName("Structural Rules")
public final class StructureRules {

  @ArchTest
  public static final ArchRule modelsDependsOnThemselves = noClasses()
      .that().resideInAPackage("..model..")
      .should().dependOnClassesThat().resideOutsideOfPackage("..model..");

  @ArchTest
  public static final ArchRule toolsDoNoDependOnCommands = noClasses()
      .that().resideInAPackage("..tools..")
      .should().dependOnClassesThat().resideInAnyPackage("..compose..", "..devcontainer..", "..shell..");

  @ArchTest
  public static final ArchRule iloDependsOnCommands = classes()
      .that().haveFullyQualifiedName("wtf.metio.ilo.Ilo")
      .should().dependOnClassesThat().resideInAnyPackage("..compose..", "..devcontainer..", "..shell..");

  @ArchTest
  public static final ArchRule noCycles = slices()
      .matching("wtf.metio.ilo.(*)..")
      .should().beFreeOfCycles();

}
