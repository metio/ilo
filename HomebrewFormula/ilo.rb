# This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
# directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
# including this file, may be copied, modified, propagated, or distributed except according to the terms contained
# in the LICENSE file.

class Ilo < Formula
  desc "Manage reproducible build environments"
  version "2022.3.14"
  homepage "https://ilo.projects.metio.wtf/"
  license "CC0"
  url "https://github.com/metio/ilo/releases/download/2022.3.14/ilo-2022.3.14-mac.zip"
  sha256 "c0227b701a44adfae1d04368aa5603f9492114ffd8065eee23e930295b6b2255"

  depends_on "podman" => :optional
  depends_on "docker" => :optional
  depends_on "nerdctl" => :optional

  def install
    bin.install "ilo-2022.3.14/ilo"
  end

  test do
    system "#{bin}/ilo", "--help"
  end
end
