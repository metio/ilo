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

**Important**: Make sure that you have **rootless** docker installed, otherwise you will probably run into file permission problems. Take a look at the [official documentation](https://docs.docker.com/engine/security/rootless/).

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
