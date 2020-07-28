/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchRules;
import com.tngtech.archunit.junit.ArchTest;

@AnalyzeClasses(packages = "wtf.metio.ilo", importOptions = ImportOption.DoNotIncludeTests.class)
public final class ArchitectureTest {

  @ArchTest
  public static final ArchRules codingRules = ArchRules.in(CodingRules.class);

  @ArchTest
  public static final ArchRules structureRules = ArchRules.in(StructureRules.class);

}
