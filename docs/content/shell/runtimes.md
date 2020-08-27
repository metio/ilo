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

Configure the docker runtime with its [environment variables](https://docs.docker.com/engine/reference/commandline/cli/#environment-variables).

## Podman

Force `ilo` to use [podman](https://podman.io/) like this:

```shell script
$ ilo shell --runtime podman

# alias
$ ilo shell --runtime p
```

## LXC/LXD

**WORK IN PROGRESS**

Force `ilo` to use [lxd](https://linuxcontainers.org/lxd/introduction/) like this:

```shell script
$ ilo shell --runtime lxd

# aliases
$ ilo shell --runtime lxc
$ ilo shell --runtime l
```
