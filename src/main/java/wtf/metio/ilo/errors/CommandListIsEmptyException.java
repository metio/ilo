/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

public final class CommandListIsEmptyException extends BusinessException {

  public CommandListIsEmptyException(final IndexOutOfBoundsException exception) {
    super(102, exception, "The generated command list is empty - this is a bug in ilo!");
  }

}
