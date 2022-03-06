---
title: Build Environments
date: 2020-04-13
menu:
  main:
    parent: usage
    identifier: usage_build_envs
    weight: 101
categories:
- usage
tags:
- build
- env
---

`ilo` allows you to define your build environment either in a [Containerfile/Dockerfile](https://docs.docker.com/engine/reference/builder/) or any other [OCI Image](https://github.com/opencontainers/image-spec/blob/master/spec.md) compliant way. In contrast to [toolbx](https://containertoolbx.org/), `ilo` relies on immutable containers which makes it easier to share those images across your team. `ilo` uses the same mechanism to define build environments that developers are already using to define their application run environments. Therefore, onboarding and adapting container based build environments should be easy for most teams.

As an example, consider the following Containerfile that is based on the official [Maven image](https://hub.docker.com/_/maven) and extends that with another binary ([hugo](https://gohugo.io/) in this case).

```shell script
# write some Containerfile
$ cat your.containerfile
FROM maven:3-openjdk-11-slim

RUN apt-get update && apt-get install hugo -y
```

This image can be build just like any other image with your typical tooling, e.g. using [podman](https://podman.io/):

```shell script
$ podman build --tag your.image:your.tag --file your.containerfile path/to/build/context
```

The idea behind `ilo` is that you use this image to start a container that mounts your project directory and is able to execute any command that you are using to build/test/package your project.
