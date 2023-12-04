/*
 * SPDX-FileCopyrightText: The ilo Authors
 * SPDX-License-Identifier: 0BSD
 */

package wtf.metio.ilo.version;

import picocli.CommandLine;

public final class VersionProvider implements CommandLine.IVersionProvider {

  @Override
  public String[] getVersion() {
    return new String[]{
        "ilo: " + Version.VERSION,
        "JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
        "OS:  ${os.name} ${os.version} ${os.arch}"
    };
  }

}
