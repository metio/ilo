/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */
package wtf.metio.ilo.errors;

public class JsonParsingException extends BusinessException {

  public JsonParsingException(final Throwable cause) {
    super(109, cause, "The devcontainer JSON file could not be parsed. Make sure it contains valid JSON.");
  }

}
