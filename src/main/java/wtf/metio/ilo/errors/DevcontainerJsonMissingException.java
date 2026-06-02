/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

public class DevcontainerJsonMissingException extends BusinessException {

  public DevcontainerJsonMissingException() {
    super(108, null, "No devcontainer JSON file found. Create one either at '.devcontainer/devcontainer.json' or '.devcontainer.json'.");
  }

}
