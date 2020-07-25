---
title: Customize Build Environment
date: 2020-04-13
menu:
  main:
    parent: shell
    identifier: shell_customize
categories:
- shell
tags:
- customize
---

The `ilo shell` command uses `fedora:latest` as its default image. In most cases `fedora:latest` will not be enough to compile/test/package/run your software. While you can install additional packages inside the container, those changes will be lost once you remove the container.

Instead `ilo` allows you to define your build environment either in a [Dockerfile](https://docs.docker.com/engine/reference/builder/) or any other [OCI Image](https://github.com/opencontainers/image-spec/blob/master/spec.md) compliant way.

Make sure your image can be accessed by everyone in your team and use `ilo shell --image your.image.here:latest` to open a new instance of your build environment.
