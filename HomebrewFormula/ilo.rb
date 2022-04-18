# This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
# directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
# including this file, may be copied, modified, propagated, or distributed except according to the terms contained
# in the LICENSE file.

class Ilo < Formula
  desc "Manage reproducible build environments"
  version "2022.4.18"
  homepage "https://ilo.projects.metio.wtf/"
  license "CC0"
  url "https://github.com/metio/ilo/releases/download/2022.4.18/ilo-2022.4.18-mac.zip"
  sha256 "5a3f79434cc194de598935af6dab577bd24801b7b4b523b344848acda8d1acd2"

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
