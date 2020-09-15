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

@DisplayName("CLI Rules")
public final class CliRules {

  @ArchTest
  public static final ArchRule cliPackageCanThrowErrors = classes()
    .that().resideInAPackage("..cli..")
    .should().dependOnClassesThat().resideInAPackage("..errors..");

}
