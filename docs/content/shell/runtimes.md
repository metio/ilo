---
title: Runtimes
date: 2020-04-13
menu:
  main:
    parent: shell
    identifier: shell_runtimes
categories:
- shell
tags:
- runtime
- docker
- podman
- lxd
---

`ilo shell` by default searches your local system for supported runtimes. It prefers `podman` over `docker` over `lxd` at the moment. In order to force the usage of a specific runtime, use the `--runtime` flag.

## Docker

Force `ilo` to use [docker](https://www.docker.com/)  like this:

```shell script
$ ilo shell --runtime docker

# alias
$ ilo shell --runtime d
```

**Important**: Many images use `root` as their default user. Mounting a local directory and writing files to it from inside the container might cause permission problems. Docker provides a **rootless** mode which alleviates those problems. Take a look at the [official documentation](https://docs.docker.com/engine/security/rootless/) for more details. In case running in a rootless configuration is not possible, either change the image you are using so that it does not use `root` anymore, or use the `--runtime-run-option` to specify the user while running the container. If all that sounds too confusing, use [podman](https://podman.io/) as your runtime instead.

## Podman

Force `ilo` to use [podman](https://podman.io/) like this:

```shell script
$ ilo shell --runtime podman

# alias
$ ilo shell --runtime p
```

## LXC/LXD

[**WORK IN PROGRESS**](https://github.com/metio/ilo/issues/41)

Force `ilo` to use [lxd](https://linuxcontainers.org/lxd/introduction/) like this:

```shell script
$ ilo shell --runtime lxd

# aliases
$ ilo shell --runtime lxc
$ ilo shell --runtime l
```
