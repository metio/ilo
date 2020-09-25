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

**Important**: In case your image uses `root` as its user, and you cannot run [rootless docker](https://docs.docker.com/engine/security/rootless/) (introduced with 19.03), use the [`--run-as`](../options) option to override the user in the image.

## Podman

Force `ilo` to use [podman](https://podman.io/) like this:

```shell script
$ ilo shell --runtime podman

# alias
$ ilo shell --runtime p
```

**Important**: In case your images uses `root` as its user, and you cannot run [rootless podman](https://github.com/containers/podman/blob/master/rootless.md), use the [`--run-as`](../options) option to override the user in the image. Take a look at this [article](https://www.redhat.com/sysadmin/behind-scenes-podman) for an in-depth example of how rootless work with podman.
