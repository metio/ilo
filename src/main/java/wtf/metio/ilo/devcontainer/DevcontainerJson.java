/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devcontainer;

import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://code.visualstudio.com/docs/remote/devcontainerjson-reference">devcontainer reference</a>
 */
final class DevcontainerJson {

  //region general
  public String name;
  public String remoteUser;
  public Map<String, String> remoteEnv;
  public String workspaceFolder;
  //endregion

  //region shell
  public String image;
  public String dockerFile;
  public String context;
  public List<Integer> forwardPorts;
  public String containerUser;
  public List<String> runArgs;
  public Map<String, String> containerEnv;
  public boolean overrideCommand;
  public Build build;
  //endregion

  //region compose
  public List<String> dockerComposeFile;
  public String service;
  public List<String> runServices;
  //endregion

  static final class Build {
    public String dockerFile;
    public String context;
    public Map<String, String> args;
    public String target;
  }

}
