---
title: Usage
date: 2020-04-13
menu: main
---

Make sure that `ilo` is [installed](./install) in your PATH or otherwise accessible from your terminal. The following subcommands are currently available:

## ilo shell

The `ilo shell` command can be used to run a single container either in interactive mode (default) or non-interactive mode (e.g. for CI builds).

```shell script
[you@hostname project-dir]$ ilo shell
[root@container project-dir]#
```

Take a look at more detailed information [here](../shell).

## ilo compose

The `ilo compose` command can be used for more complex build environments based on `docker-compose.yml` files. Use this command in case your project requires e.g. a database in order to be build.

```shell script
[you@hostname project-dir]$ ilo compose
[root@container project-dir]#
```

Take a look at more detailed information [here](../compose).

## ilo devcontainer

The `ilo devcontainer` command uses a [devcontainer](https://code.visualstudio.com/docs/remote/containers) environment.

```shell script
[you@hostname project-dir]$ ilo devcontainer
[root@container project-dir]#
```

Take a look at more detailed information [here](../devcontainer).
