# This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
# directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
# including this file, may be copied, modified, propagated, or distributed except according to the terms contained
# in the LICENSE file.

class Ilo < Formula
  desc "Manage reproducible build environments"
  version "2022.12.12"
  homepage "https://ilo.projects.metio.wtf/"
  license "CC0"
  url "https://github.com/metio/ilo/releases/download/2022.12.12/ilo-2022.12.12-mac.zip"
  sha256 "d44b88c9f045ff9d0ea566a1dab8e85f313c93b6617643461b9b61ac640ddc9a"

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
