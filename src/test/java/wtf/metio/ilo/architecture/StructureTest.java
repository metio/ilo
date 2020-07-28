/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "wtf.metio.ilo", importOptions = ImportOption.DoNotIncludeTests.class)
public final class StructureTest {

  @ArchTest
  public static final ArchRule modelsDependsOnThemselves = noClasses()
      .that().resideInAPackage("..model..")
      .should().dependOnClassesThat().resideOutsideOfPackage("..model..");

  @ArchTest
  public static final ArchRule executablesCanThrowErrors = classes()
      .that().resideInAPackage("..exec..")
      .should().dependOnClassesThat().resideInAPackage("..errors..");

}
