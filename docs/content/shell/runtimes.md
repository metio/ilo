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

`ilo shell` by default searches your local system for supported runtimes. It prefers `docker` over `podman` over `lxd` at the moment. In order to force the usage of a specific runtime, use the `--runtime` flag.

| Feature        | Docker | Podman | LXD  |
|----------------|--------|--------|------|
| run container  | yes    | yes    | yes  |
| custom image   | yes    | yes    | yes  |
| shell          | yes    | yes    | [#33](https://github.com/metio/ilo/issues/33) |
| build          | yes    | yes    | [#34](https://github.com/metio/ilo/issues/34) |
| commands       | yes    | yes    | [#35](https://github.com/metio/ilo/issues/35) |
| debug          | yes    | yes    | yes  |
| pull           | yes    | yes    | [#36](https://github.com/metio/ilo/issues/36) |
| dockerfile     | yes    | yes    | [#37](https://github.com/metio/ilo/issues/37) |
| interactive    | yes    | yes    | yes  |
| mount          | yes    | yes    | [#38](https://github.com/metio/ilo/issues/38) |
| remove image   | yes    | yes    | [#39](https://github.com/metio/ilo/issues/39) |

- **run container**: Allows to run a container.
- **custom image**: Allows to specify a custom image.
- **shell**: Allows opening up a new shell automatically.
- **build**: Allows to execute a build command within a container.
- **commands**: Allows specifying custom commands.
- **debug**: Allows to enable debug mode.
- **pull**: Allows to pull an image before opening a new shell.
- **dockerfile**: Allows to build an image locally before opening a new shell.
- **interactive**: Allows running interactively.
- **mount**: Allows to mount the project directory automatically.
- **remove image**: Allows to remove the image from your local system after the shell closes.

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

Force `ilo` to use [lxd](https://linuxcontainers.org/lxd/introduction/) like this:

```shell script
$ ilo shell --runtime lxd

# aliases
$ ilo shell --runtime lxc
$ ilo shell --runtime l
```
