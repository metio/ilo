/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.errors;

public class DevfileYamlMissingException extends BusinessException {

  public DevfileYamlMissingException() {
    super(111, null, "No devfile YAML file found. Create one either at 'devfile.yaml' or '.devfile.yaml'.");
  }

}
