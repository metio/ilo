/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.architecture;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@DisplayName("Layer Rules")
public final class LayerRules {

  @ArchTest
  public static final ArchRule layerAccessControl = layeredArchitecture()
    .consideringAllDependencies()
    .withOptionalLayers(true)
    .layer("CLI").definedBy("wtf.metio.ilo.cli..")
    .layer("Commands").definedBy("wtf.metio.ilo.compose..", "wtf.metio.ilo.devcontainer..", "wtf.metio.ilo.shell..")
    .layer("Errors").definedBy("wtf.metio.ilo.errors..")
    .layer("Factories").definedBy("wtf.metio.ilo.factories..")
    .layer("Models").definedBy("wtf.metio.ilo.model..")
    .layer("Tools").definedBy("wtf.metio.ilo.tools..")
    .layer("Utils").definedBy("wtf.metio.ilo.utils..")
    .whereLayer("Models").mayOnlyBeAccessedByLayers("Commands", "CLI", "Tools");

}
