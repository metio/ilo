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
| shell          | yes    | yes    | no   |
| build          | yes    | yes    | no   |
| commands       | yes    | yes    | no   |
| debug          | yes    | yes    | yes  |
| pull           | yes    | yes    | no   |
| dockerfile     | yes    | yes    | no   |
| interactive    | yes    | yes    | yes  |
| no-interactive | yes    | yes    | no   |
| mount          | yes    | yes    | no   |
| no-mount       | yes    | yes    | no   |
| remove image   | yes    | yes    | no   |

- **run container**: Allows to run a container.
- **custom image**: Allows to specify a custom image.
- **shell**: Allows opening up a new shell automatically.
- **build**: Allows to execute a build command within a container.
- **commands**: Allows specifying custom commands.
- **debug**: Allows to enable debug mode.
- **pull**: Allows to pull an image before opening a new shell.
- **dockerfile**: Allows to build an image locally before opening a new shell.
- **interactive**: Allows running interactively.
- **no-interactive**: Allows running non-interactive.
- **mount**: Allows to mount the project directory automatically.
- **no-mount**: Allows disabling mounting the project directory.
- **remove image**: Allows to remove the image from your local system after the shell closes.

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

## LXC/LXD

Force `ilo` to use [lxd](https://linuxcontainers.org/lxd/introduction/) like this:

```shell script
$ ilo shell --runtime lxd

# alias
$ ilo shell --runtime l
```
