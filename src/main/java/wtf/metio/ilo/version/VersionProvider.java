/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
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
