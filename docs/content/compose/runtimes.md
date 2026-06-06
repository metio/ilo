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

Auto-selection prefers Docker Compose v2 (`docker compose`), then `docker-compose` (v1), then `podman-compose` — and only picks Docker Compose v2 when the plugin is actually installed. Note that this differs from `ilo shell`, which prefers Podman, so on a host with both Docker and Podman the two commands may pick different runtimes; pass `--runtime` (or use `--debug` to see the exact command, including which runtime was chosen) if you want them aligned.

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
$ ilo compose --runtime podman-compose

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
