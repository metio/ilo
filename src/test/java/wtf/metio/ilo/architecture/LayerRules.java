/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
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
      .layer("Commands").definedBy("wtf.metio.ilo.shell..")
      .layer("Errors").definedBy("wtf.metio.ilo.errors..")
      .layer("Models").definedBy("wtf.metio.ilo.model..")
      .layer("Tools").definedBy("wtf.metio.ilo.tools..")
      .layer("Utils").definedBy("wtf.metio.ilo.utils..")
      .whereLayer("Models").mayOnlyBeAccessedByLayers("Commands", "CLI", "Tools");

}
