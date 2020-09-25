---
title: Customize Environment
date: 2020-04-13
menu:
  main:
    parent: devcontainer
    identifier: devcontainer_customize
categories:
- devcontainer
tags:
- customize
---

`ilo` allows you to define your build environment either in a [Dockerfile](https://docs.docker.com/engine/reference/builder/) or any other [OCI Image](https://github.com/opencontainers/image-spec/blob/master/spec.md) compliant way.

Make sure to specify `your.image.here:some-tag` as the image in your `devcontainer.json` file. Take a look at the [reference documentation](https://code.visualstudio.com/docs/remote/devcontainerjson-reference) for all available options for that JSON file.
