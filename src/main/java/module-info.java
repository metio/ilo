/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

module wtf.metio.ilo {

  requires info.picocli;
  requires tools.jackson.databind;
  requires tools.jackson.dataformat.yaml;
  requires wtf.metio.devcontainer;

  opens wtf.metio.ilo to info.picocli;
  opens wtf.metio.ilo.compose to info.picocli;
  opens wtf.metio.ilo.devcontainer to info.picocli;
  opens wtf.metio.ilo.devfile to info.picocli;
  opens wtf.metio.ilo.shell to info.picocli;
  opens wtf.metio.ilo.version to info.picocli;

}
