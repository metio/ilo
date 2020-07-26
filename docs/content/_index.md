---
title: ilo
date: 2020-04-13
---

# ilo

> manage reproducible build environments 

`ilo` is a [toolbox](https://github.com/containers/toolbox) inspired tool to create/manage [reproducible build environments](https://reproducible-builds.org/) based on [OCI](https://www.opencontainers.org/) container images.

## Features

### Reproducible Build Environments

Thanks to containers, `ilo` can fully encapsulate the necessary tools required to build your project. Therefore, making it easy to reproduce the build output of any project. Custom tooling, a specific version of a compiler, or anything else required to build a project are no longer showstoppers, but rather implementation details.

### Per-Project Dependencies

`ilo` recognizes that lots of projects have their own unique build requirements. Instead of forcing users to install all required tooling into their local system, `ilo` moves all project dependencies into a container. In case you want to clean up your computer, just remove the container image! `ilo` will automatically recreate a build environment for your project the next time you need it.

### Teamwork

Onboarding new team members into a big projects with complex build requirements can be a hassle. `ilo`'s container approach reduces the amount of work required to get new members up to speed - install `ilo`, clone your project, and you're good to go. `ilo` supports multiple ways to share immutable build environments with your team in order reproduce a project.

### Cross-Platform

`ilo` is available for [Linux](https://www.kernel.org/), [Mac](https://www.apple.com/macos/), and [Windows](https://www.microsoft.com/en-us/windows). It supports a wide range of runtimes which makes it easy to both add and remove `ilo` from your project. It plays nicely with tools already available on your local system - use your favorite IDE to write code!

## Users

Want to try `ilo` for your project? Take a look at the [usage guide](./usage).

## Contributors

Interested in contributing to `ilo`? Take a look at the [contributor guide](./contributors)
