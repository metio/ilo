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
- nerdctl
- container
---

`ilo shell` by default searches your local system for supported runtimes. In order to force the usage of a specific runtime, use the `--runtime` flag or set the `ILO_SHELL_RUNTIME` environment variable in your system. The `--runtime` flag overwrites the environment variable.

## Docker

Force `ilo` to use [docker](https://www.docker.com/) like this:

```console
$ ilo shell --runtime docker

# use alias
$ ilo shell --runtime d

# use env variable
$ ILO_SHELL_RUNTIME=docker ilo shell
```

## nerdctl

Force `ilo` to use [nerdctl](https://github.com/containerd/nerdctl) like this:

```console
$ ilo shell --runtime nerdctl

# use alias
$ ilo shell --runtime n

# use env variable
$ ILO_SHELL_RUNTIME=nerdctl ilo shell
```

## Podman

Force `ilo` to use [podman](https://podman.io/) like this:

```console
$ ilo shell --runtime podman

# use alias
$ ilo shell --runtime p

# use env variable
$ ILO_SHELL_RUNTIME=podman ilo shell
```

## Apple Container

Force `ilo` to use Apple's [container](https://github.com/apple/container) like this:

```console
$ ilo shell --runtime container

# use alias
$ ilo shell --runtime c

# use env variable
$ ILO_SHELL_RUNTIME=container ilo shell
```

This runtime runs on macOS only (Apple silicon, macOS 26 or newer) and needs its services started once per boot with `container system start`; `ilo` does not start them for you.

A single shell works as expected. Sharing one container across several terminals at once is not supported: this runtime exposes no way to tell whether another terminal still has the container open, so the first session to exit stops it. Pass `--keep-running-on-exit` while a second terminal is open to keep the container alive.

## Auto Selection

If not otherwise specified, `ilo` always picks runtimes in this order, depending on which are available on your system:

1. podman
2. nerdctl
3. docker
4. container (macOS only)
