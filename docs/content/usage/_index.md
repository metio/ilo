---
title: Usage
date: 2020-04-13
menu: main
---

Make sure that `ilo` is [installed](./install) in your path or otherwise accessible from your terminal. The following subcommands are currently available:

## ilo shell

The `ilo shell` command can be used to run a single container either in interactive mode (default) or non-interactive mode (e.g. for CI builds).
It defaults to use the `fedora:latest` image and executes `/bin/bash` inside the running container to get a running shell.
It automatically mounts the current working directory (e.g. your project directory) and stops/removes the container once you exit the shell. `ilo shell` can be used with either [podman](https://podman.io/) (default) or [docker](https://www.docker.com/products/container-runtime) by using the `--runtime` switch. If no runtime is specified `ilo` will auto-detect available runtimes and prefer `podman` over `docker`.
Use `ilo shell --help` to get a list of all options, and their default values.

```shell script
[you@hostname project-dir]$ ilo shell
[root@container project-dir]#
```

## ilo compose

The `ilo compose` command can be used for more complex build environments based on docker-compose.yml files.
Use this command in case your project requires e.g. a database in order to be build. `ilo compose` does not mount any directories by default, nor does it automatically execute a specific command.
It defaults to run with podman-compose but can be used with docker-compose as well by using the `--runtime` switch.
Use `ilo compose --help` to get a list of all options, and their default values.

```shell script
[you@hostname project-dir]$ ilo compose
[root@container project-dir]#
```

## ilo devcontainer
