---
title: Options
date: 2020-04-13
menu:
  main:
    parent: shell
    identifier: shell_options
categories:
- shell
tags:
- options
---

The `ilo shell` command can be configured with the following command line options.

## `--runtime`

The `--runtime` option can be used to force the usage of a specific runtime. See [docker](./docker), [podman](./podman), or [lxd](./lxd) for details.

```shell script
# force to use docker
$ ilo shell --runtime docker

# force to use podman
$ ilo shell --runtime podman

# force to use lxd
$ ilo shell --runtime lxd

# auto select
$ ilo shell
```

In case no `--runtime` is specified, `ilo shell` will automatically select one of the runtimes installed on your local system and prefers `docker` over `podman` over `lxd`.

## `--image`

The OCI image to use, defaults to `fedora:latest`. In case `--dockerfile` is given as well, this defines the name of the created image.

```shell script
# use node:12.18.2
$ ilo shell --image node:12.18.2

# use fedora:latest
$ ilo shell

# build image my.awesome.image:latest & open shell in it
$ ilo shell --image my.awesome.image:latest --dockerfile build/Dockerfile
```

## `--pull`

The `--pull` option can be used to pull the specified image before opening a new shell. This is especially useful for teams using a `latest` tag for their image.

```shell script
# pull image before opening shell
$ ilo shell --pull

# do not pull image before opening shell
$ ilo shell --pull false

# do not pull image before opening shell
$ ilo shell 
```

By default, `--pull` is not enabled.
