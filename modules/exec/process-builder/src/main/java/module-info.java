/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

import wtf.metio.ilo.exec.api.ExecutablesProvider;
import wtf.metio.ilo.exec.pb.ProcessBuilderExecutablesProvider;

module wtf.metio.ilo.exec.pb {

  requires wtf.metio.ilo.exec.api;

  provides ExecutablesProvider
      with ProcessBuilderExecutablesProvider;

}
