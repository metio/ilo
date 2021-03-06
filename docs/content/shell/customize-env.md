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

The `ilo shell` command uses the `fedora:latest` image by default. In most cases `fedora:latest` will not be enough to compile/test/package/run your software. While you can install additional packages inside the container, those changes will be lost once you remove the container. In contrast to [toolbox](https://github.com/containers/toolbox), `ilo` relies on immutable containers.

`ilo` allows you to define your build environment either in a [Dockerfile](https://docs.docker.com/engine/reference/builder/) or any other [OCI Image](https://github.com/opencontainers/image-spec/blob/master/spec.md) compliant way.

Make sure your image can be accessed by everyone in your team and use `ilo shell your.image.here:latest` to open a new instance of your build environment.

```shell script
# write some Dockerfile
$ cat Dockerfile
FROM maven:slim

RUN apt-get update && apt-get install hugo -y

CMD ["jshell"]
```

Build the image like this:

```shell script
# build image
$ docker build --tag your.image:latest .
```

Once completed, you can open a new shell like this:

```shell script
# use image, jump into 'jshell'
$ ilo shell your.image:latest
```

## Local environments

In case you cannot push an image to a registry (e.g. you don't have one), you can use the `--dockerfile` option to let `ilo` build your image first before entering a new shell. The above example can thus be simplified to:

```shell script
$ ilo shell --dockerfile your.dockerfile your.image:latest
```
