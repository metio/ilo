---
title: Customize Environment
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

The `ilo shell` command uses the `fedora:latest` image by default. In most cases `fedora:latest` will not be enough to compile/test/package/run your software. While you can install additional packages inside the container, those changes will be lost once you remove the container. In contrast to [toolbx](https://containertoolbx.org/), `ilo` relies on immutable containers.

`ilo` allows you to define your build environment either in a [Containerfile/Dockerfile](https://docs.docker.com/engine/reference/builder/) or any other [OCI Image](https://github.com/opencontainers/image-spec/blob/master/spec.md) compliant way.

Make sure your image can be accessed by everyone in your team and use `ilo shell your.image.here:some.tag` to open a new instance of your build environment.

```shell script
# write some Containerfile
$ cat Containerfile
FROM maven:slim

RUN apt-get update && apt-get install hugo -y

CMD ["jshell"]
```

Build the image like this:

```shell script
# build image
$ docker build --tag your.image:your.tag --file Containerfile .
```

Once completed, you can open a new shell like this:

```shell script
# use image, jump into 'jshell'
$ ilo shell your.image:your.tag
```

## Local environments

In case you cannot push an image to a registry (e.g. you don't have one), you can use the `--containerfile`/`--dockerfile` option to let `ilo` build your image first before entering a new shell. The above example can thus be simplified to:

```shell script
$ ilo shell --containerfile your.containerfile your.image:your.tag
```
