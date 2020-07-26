---
title: Usage
date: 2020-04-13
menu: main
---

Make sure that `ilo` is [installed](./install) in your path or otherwise accessible from your terminal. The following subcommands are currently available:

## ilo shell

The `ilo shell` command can be used to run a single container either in interactive mode (default) or non-interactive mode (e.g. for CI builds).

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
