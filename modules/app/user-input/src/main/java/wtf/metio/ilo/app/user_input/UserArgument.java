/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

package wtf.metio.ilo.app.user_input;

public class UserArgument<T> {

  public final String name;
  public final T defaultValue;
  public final String description;

  public UserArgument(final String name, final T defaultValue, final String description) {
    this.name = name;
    this.defaultValue = defaultValue;
    this.description = description;
  }

  public static <T> UserArgument<T> arg(final String name, final T defaultValue, final String description) {
    return new UserArgument<>(name, defaultValue, description);
  }

  @Override
  public String toString() {
    return "--" + name + "\t" + description + " Defaults to [" + defaultValue + "]";
  }
}
