# This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
# directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
# including this file, may be copied, modified, propagated, or distributed except according to the terms contained
# in the LICENSE file.

class Ilo < Formula
  desc "Manage reproducible build environments"
  version "2022.11.28"
  homepage "https://ilo.projects.metio.wtf/"
  license "CC0"
  url "https://github.com/metio/ilo/releases/download/2022.11.28/ilo-2022.11.28-mac.zip"
  sha256 "d66d5c8dafae22a22b38d0ed753884c4de54c4ecc961f04210586146aa384cb1"

  depends_on "podman" => :optional
  depends_on "docker" => :optional
  depends_on "nerdctl" => :optional

  def install
    bin.install "ilo"
  end

  test do
    system "#{bin}/ilo", "--help"
  end
end
