---
title: Runtimes
date: 2020-04-13
menu:
  main:
    parent: compose
    identifier: compose_runtimes
categories:
- compose
tags:
- runtime
- docker-compose
- footloose
- podman-compose
- pods-compose
- vagrant
---

`ilo compose` by default searches your local system for supported runtimes. In order to force the usage of a specific runtime, use the `--runtime` flag.

## docker-compose

Force `ilo` to use [docker-compose](https://docs.docker.com/compose/) like this:

```console
$ ilo compose --runtime docker-compose

# alias
$ ilo compose --runtime dc
```

## podman-compose

Force `ilo` to use [podman-compose](https://github.com/containers/podman-compose) like this:

```console
$ ilo compose --runtime podman-copose

# alias
$ ilo compose --runtime pc
```

## Auto Selection

If not otherwise specified, `ilo` always picks runtimes in this order, depending on which are available on your system:

1. docker-compose
2. podman-compose
