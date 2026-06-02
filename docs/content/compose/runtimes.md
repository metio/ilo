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
- podman-compose
- docker
---

`ilo compose` by default searches your local system for supported runtimes. In order to force the usage of a specific runtime, use the `--runtime` flag or set the `ILO_COMPOSE_RUNTIME` environment variable in your system. The `--runtime` flag overwrites the environment variable.

## docker-compose

Force `ilo` to use [docker-compose](https://docs.docker.com/compose/) like this:

```console
$ ilo compose --runtime docker-compose

# use alias
$ ilo compose --runtime dc

# use env variable
$ ILO_COMPOSE_RUNTIME=docker-compose ilo compose
```

## podman-compose

Force `ilo` to use [podman-compose](https://github.com/containers/podman-compose) like this:

```console
$ ilo compose --runtime podman-copose

# use alias
$ ilo compose --runtime pc

# use env variable
$ ILO_COMPOSE_RUNTIME=podman-compose ilo compose
```

## docker

Force `ilo` to use [docker](https://www.docker.com/) in [compose v2 mode](https://docs.docker.com/compose/cli-command/) like this:

```console
$ ilo compose --runtime docker

# use alias
$ ilo compose --runtime d

# use env variable
$ ILO_COMPOSE_RUNTIME=docker ilo compose
```

## Auto Selection

If not otherwise specified, `ilo` always picks runtimes in this order, depending on which are available on your system:

1. docker-compose
2. podman-compose
3. docker
