# This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
# directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
# including this file, may be copied, modified, propagated, or distributed except according to the terms contained
# in the LICENSE file.

class Ilo < Formula
  desc "Manage reproducible build environments"
  version "2023.2.20"
  homepage "https://ilo.projects.metio.wtf/"
  license "CC0"
  url "https://github.com/metio/ilo/releases/download/2023.2.20/ilo-2023.2.20-mac.zip"
  sha256 "4c7dbdf09b7ae9965b6c1322b238bdaab1fb51a88800b4e6549e4128374d68d6"

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
