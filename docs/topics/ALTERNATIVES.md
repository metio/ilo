# Alternatives

It's highly likely that `ilo` does not provide the functionality you are looking for. Here is a list of alternatives that might cover your needs:

- [toolbox](https://github.com/containers/toolbox): Toolbox is a tool that offers a familiar package based environment for developing and debugging software that runs fully unprivileged using Podman.
- [gitian](https://gitian.org/): Gitian is a secure source-control oriented software distribution method.
This means you can download trusted binaries that are verified by multiple builders.
- [rbm](https://rbm.torproject.org/): Reproducible Build Manager (rbm) is a tool that helps you create and build packages for multiple linux distributions, and automate the parts that can be automated.
It includes options to run the build in a defined environement to allow reproducing the build.
- [rez](https://github.com/nerdvegas/rez): Rez is a cross-platform package manager with a difference.
Using Rez you can create standalone environments configured for a given set of packages.
- [bleeding-rez](https://github.com/mottosso/bleeding-rez): Rez is a command-line utility for Windows, Linux and MacOS, solving the problem of creating a reproducible environment for your software projects on any machine in any pre-existing environment.
- [rebuild](http://rbld.io/): A strategic approach to managing build and test environments for IoT and embedded development.
- [ducible](https://github.com/jasonwhite/ducible): This is a tool to make builds of Portable Executables (PEs) and PDBs reproducible.
- [buildpacks](https://buildpacks.io/): Buildpacks are pluggable, modular tools that translate source code into OCI images.
- [nix](https://nixos.org/nix/): Nix makes it trivial to set up and share build environments for your projects, regardless of what programming languages and tools you’re using.
- [kin](https://github.com/jacobmealey/kin): Kin is a project that harnesses the power of Docker to give developers access to other distros without having to dual boot or run a virtual machine!
- [mkdkr](https://github.com/rosineygp/mkdkr): Super small and powerful framework for build CI pipeline, scripted with Makefile and isolated with docker.

## Feature Comparison

| Tool    | Language Support | Build Environment | Build Once |
|---------|------------------|-------------------|------------|
| ilo     | any              | yes               | yes        |
| toolbox | any installable  | yes               | no         |
