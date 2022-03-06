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
- nerdctl
---

`ilo shell` by default searches your local system for supported runtimes. It prefers `podman` over `docker` over `lxd` at the moment. In order to force the usage of a specific runtime, use the `--runtime` flag.

## Docker

Force `ilo` to use [docker](https://www.docker.com/)  like this:

```shell script
$ ilo shell --runtime docker

# alias
$ ilo shell --runtime d
```

## Podman

Force `ilo` to use [podman](https://podman.io/) like this:

```shell script
$ ilo shell --runtime podman

# alias
$ ilo shell --runtime p
```

## nerdctl

Force `ilo` to use [nerdctl](https://github.com/containerd/nerdctl) like this:

```shell script
$ ilo shell --runtime nerdctl

# alias
$ ilo shell --runtime n
```

## Auto Selection

If not otherwise specified, `ilo` always picks runtimes in this order, depending on which are available on your system:

1. podman
2. nerdctl
3. docker
