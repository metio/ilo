---
title: Customize Build Environment
date: 2020-04-13
menu:
  main:
    parent: compose
    identifier: compose_customize
categories:
- compose
tags:
- customize
---

`ilo` allows you to define your build environment either in a [Dockerfile](https://docs.docker.com/engine/reference/builder/) or any other [OCI Image](https://github.com/opencontainers/image-spec/blob/master/spec.md) compliant way.

If you are using `ilo compose`, make sure to specify `your.image.here:latest` as the image in your `docker-compose.yml` file.
