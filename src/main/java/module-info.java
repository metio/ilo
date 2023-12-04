/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

module wtf.metio.ilo {

  requires info.picocli;

  opens wtf.metio.ilo to info.picocli;
  opens wtf.metio.ilo.shell to info.picocli;
  opens wtf.metio.ilo.version to info.picocli;

}
