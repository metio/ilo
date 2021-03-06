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

`ilo compose` by default searches your local system for supported runtimes. It prefers `docker-compose` over `podman-compose` over `pods-compose` over `footloose` over `vagrant` at the moment. In order to force the usage of a specific runtime, use the `--runtime` flag.

## docker-compose

Force `ilo` to use [docker-compose](https://docs.docker.com/compose/) like this:

```shell script
$ ilo shell --runtime docker-compose

# alias
$ ilo shell --runtime dc
```

## podman-compose

Force `ilo` to use [podman-compose](https://github.com/containers/podman-compose) like this:

```shell script
$ ilo shell --runtime podman-copose

# alias
$ ilo shell --runtime pc
```
