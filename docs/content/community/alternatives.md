---
title: Alternatives
date: 2020-04-13
menu:
  main:
    parent: 'Community'
categories:
- Community
tags:
- alternatives
---

It's highly likely that `ilo` does not provide the functionality you are looking for. Here is a list of alternatives that might cover your needs:

- [toolbox](https://github.com/containers/toolbox): Toolbox is a tool that offers a familiar package based environment for developing and debugging software that runs fully unprivileged using Podman.
- [gitian](https://gitian.org/): Gitian is a secure source-control oriented software distribution method.
- [rbm](https://rbm.torproject.org/): Reproducible Build Manager (rbm) is a tool that helps you create and build packages for multiple linux distributions, and automate the parts that can be automated.
- [rez](https://github.com/nerdvegas/rez): Rez is a cross-platform package manager with a difference.
- [bleeding-rez](https://github.com/mottosso/bleeding-rez): Rez is a command-line utility for Windows, Linux and MacOS, solving the problem of creating a reproducible environment for your software projects on any machine in any pre-existing environment.
- [rebuild](http://rbld.io/): A strategic approach to managing build and test environments for IoT and embedded development.
- [ducible](https://github.com/jasonwhite/ducible): This is a tool to make builds of Portable Executables (PEs) and PDBs reproducible.
- [buildpacks](https://buildpacks.io/): Buildpacks are pluggable, modular tools that translate source code into OCI images.
- [nix](https://nixos.org/nix/): Nix makes it trivial to set up and share build environments for your projects, regardless of what programming languages and tools you’re using.
- [kin](https://github.com/jacobmealey/kin): Kin is a project that harnesses the power of Docker to give developers access to other distros without having to dual boot or run a virtual machine!
- [mkdkr](https://github.com/rosineygp/mkdkr): Super small and powerful framework for build CI pipeline, scripted with Makefile and isolated with docker.
- [virtualenv](https://virtualenv.pypa.io/en/stable/): virtualenv is a tool to create isolated Python environments.
- [moot](https://github.com/Zlika/moot): A shell script to easily select, download and run the versions of Maven and JDK you want for your build.
- [paketo](https://paketo.io/): Modular Buildpacks, written in Go. Paketo Buildpacks provide language runtime support for applications. They leverage the Cloud Native Buildpacks framework to make image builds easy, performant, and secure.
- [earthly](https://github.com/earthly/earthly): run all your builds containerized
- [gitpod](https://www.gitpod.io/): Describe your dev environment as code and get fully prebuilt, ready-to-code development environments for any GitLab, GitHub, and Bitbucket project.
- [vagrant](https://www.vagrantup.com/): Vagrant is a tool for building and managing virtual machine environments in a single workflow.
- [subuser](https://subuser.org/): Subuser turns Docker containers into normal linux programs
- [bob](https://bobbuildtool.dev/): Bob is a functional build automation tool with an emphasisis on cross compilation. It is intended for complex embedded projects and thus focuses on reproducible builds while still being nice to developers in agile environments.

## Feature Comparison

| Tool       | Language Support | Build Environment | Build Once | Linux | Mac | Windows |
|------------|------------------|-------------------|------------|-------|-----|---------|
| ilo        | any              | yes               | yes        | yes   | yes | yes     |
| toolbox    | any installable  | yes               | no         | yes   | no  | no      |
| virtualenv | Python           | yes               | no         | yes   | yes | yes     |
| subuser    | any              | yes               | no         | yes   | no  | no      |
