{
  description = "Manage reproducible build environments";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs";
  };

  outputs = { self, nixpkgs }: {

    packages.x86_64-linux.default =
      with import nixpkgs { system = "x86_64-linux"; };
      stdenv.mkDerivation {
        name = "ilo";
        src = self;
        buildPhase = "curl --location https://github.com/metio/ilo/releases/download/2022.3.7/ilo-2022.3.7-linux.zip --output ilo.zip; unzip ilo.zip";
        installPhase = "mkdir -p $out/bin; install -t $out/bin ilo-2022.3.7/ilo";
      };

  };
}
