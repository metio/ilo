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

  // ilo's actual layering, top (entry point) to bottom (foundational leaves):
  //
  //   Application (Ilo)
  //     -> CompositeCommands (devcontainer, devfile — thin commands that delegate to base commands)
  //     -> BaseCommands (compose, shell — the self-contained command families)
  //     -> Model, OS
  //     -> CLI
  //     -> Errors, Utils, Version (leaves: depend on nothing else in ilo)
  //
  // Each 'mayOnlyBeAccessedByLayers' lists exactly the layers that depend on that layer today; a layer
  // may always use the ones below it and never the ones above (so cli cannot reach into the commands,
  // os cannot reach the model, the leaves depend on nothing in ilo, etc.).
  @ArchTest
  public static final ArchRule layerAccessControl = layeredArchitecture()
      .consideringAllDependencies()
      .withOptionalLayers(true)
      .layer("Application").definedBy("wtf.metio.ilo")
      .layer("CompositeCommands").definedBy("wtf.metio.ilo.devcontainer..", "wtf.metio.ilo.devfile..")
      .layer("BaseCommands").definedBy("wtf.metio.ilo.compose..", "wtf.metio.ilo.shell..")
      .layer("Model").definedBy("wtf.metio.ilo.model..")
      .layer("OS").definedBy("wtf.metio.ilo.os..")
      .layer("CLI").definedBy("wtf.metio.ilo.cli..")
      .layer("Errors").definedBy("wtf.metio.ilo.errors..")
      .layer("Utils").definedBy("wtf.metio.ilo.utils..")
      .layer("Version").definedBy("wtf.metio.ilo.version..")
      .whereLayer("Application").mayNotBeAccessedByAnyLayer()
      .whereLayer("CompositeCommands").mayOnlyBeAccessedByLayers("Application")
      .whereLayer("BaseCommands").mayOnlyBeAccessedByLayers("Application", "CompositeCommands")
      .whereLayer("Model").mayOnlyBeAccessedByLayers("BaseCommands", "CompositeCommands")
      .whereLayer("OS").mayOnlyBeAccessedByLayers("BaseCommands", "CompositeCommands")
      .whereLayer("CLI").mayOnlyBeAccessedByLayers("Application", "BaseCommands", "CompositeCommands", "Model", "OS")
      .whereLayer("Errors").mayOnlyBeAccessedByLayers("Application", "BaseCommands", "CompositeCommands", "CLI", "Model")
      .whereLayer("Utils").mayOnlyBeAccessedByLayers("BaseCommands", "CompositeCommands", "OS")
      .whereLayer("Version").mayOnlyBeAccessedByLayers("Application", "BaseCommands", "CompositeCommands");

}
