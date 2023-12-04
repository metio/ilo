/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.errors;

import java.util.List;
import java.util.stream.Collectors;

public final class CommandListContainsNullException extends BusinessException {

  public CommandListContainsNullException(final NullPointerException exception, final List<String> args) {
    super(101, exception, args.stream().collect(Collectors.joining(", ", "[", "]")));
  }

}
