---
title: Options
date: 2020-04-13
menu:
  main:
    parent: compose
    identifier: compose_options
categories:
- compose
tags:
- options
---

The `ilo compose` command can be configured with the following command line options. From your terminal, use `ilo compose --help` to get a list of all options, and their default values.

## `--runtime`

The `--runtime` option can be used to force the usage of a specific runtime. See [runtimes](../runtimes) for details.

```shell script
# force to use docker-compose
$ ilo compose --runtime docker-compose

# force to use podman-compose
$ ilo compose --runtime podman-compose

# force to use pods-compose
$ ilo compose --runtime pods-compose

# force to use footloose
$ ilo compose --runtime footloose

# force to use vagrant
$ ilo compose --runtime vagrant

# auto select
$ ilo compose
```

In case no `--runtime` is specified, `ilo compose` will automatically select one of the runtimes installed on your local system and prefers `docker-compose` over `podman-compose` over `pods-compose` over `footloose` over `vagrant` at the moment.

## `--pull`

The `--pull` option can be used to pull images before opening a new shell. This is especially useful for teams using a `latest` tag for their image. The image will only pulled in case the registry contains a newer image than locally available.

```shell script
# pull image before opening shell
$ ilo compose --pull

# do not pull image before opening shell
$ ilo compose --pull=false

# do not pull image before opening shell
$ ilo compose
```

By default, `--pull` is not enabled.

## `--interactive`

The `--interactive` option can be used to control whether you want an interactive session (default) or just want to execute a single command (e.g. during CI builds).

```shell script
# run interactively
$ ilo compose --interactive

# run non-interactive
$ ilo compose --no-interactive
$ ilo compose --interactive=false

# run interactively
$ ilo compose
```

By default, `--interactive` is enabled.

## `--build`

The `--build` option can be used to build images before opening a new shell. Use this option in case you rely on local Dockerfiles.

```shell script
# build images before opening shell
$ ilo compose --build

# do not build images before opening shell
$ ilo compose --build=false

# do not build images before opening shell
$ ilo compose
```

By default, `--build` is not enabled.

## `--file`

The `--file` option can be used to specify the file that contains your container definitions, e.g. a `docker-compose.yml` file.

```shell script
# use custom location
$ ilo compose --file /path/to/some/file.yaml

# use default location
$ ilo compose
```

By default, `--file` is set to `docker-compose.yml`.


## `--runtime-option`

The `--runtime-option` option can be specified multiple times and contains options for the underlying runtime.

```shell script
# specify custom option(s)
$ ilo compose --runtime-option=--no-ansi
$ ilo compose --runtime-option=--no-ansi --runtime-option=--tls

# do not specify custom option
$ ilo compose
```

By default, `--runtime-option` is set to an empty array.

## `--runtime-pull-option`

The `--runtime-pull-option` can be specified multiple times and contains options for the runtimes' `pull` command.

```shell script
# specify custom option(s)
$ ilo compose --runtime-pull-option=--ignore-pull-failures
$ ilo compose --runtime-pull-option=--ignore-pull-failures --runtime-pull-option=--quiet

# do not specify custom option
$ ilo compose
```

By default, `--runtime-pull-option` is set to an empty array.

## `--runtime-build-option`

The `--runtime-build-option` can be specified multiple times and contains options for the runtimes' `build` command.

```shell script
# specify custom option(s)
$ ilo compose --runtime-build-option=--compress
$ ilo compose --runtime-build-option=--compress --runtime-build-option=--no-cache

# do not specify custom option
$ ilo compose
```

By default, `--runtime-build-option` is set to an empty array.

## `--runtime-run-option`

The `--runtime-run-option` can be specified multiple times and contains options for the runtimes' `run` command.

```shell script
# specify custom option(s)
$ ilo compose --runtime-run-option=--no-deps
$ ilo compose --runtime-run-option=--no-deps --runtime-run-option=--use-aliases

# do not specify custom option
$ ilo compose
```

By default, `--runtime-run-option` is set to an empty array.

## `--runtime-cleanup-option`

The `--runtime-cleanup-option` can be specified multiple times and contains options for the runtimes' cleanup command, e.g. `down` for `docker-compose`.

```shell script
# specify custom option(s)
$ ilo compose --runtime-cleanup-option=--remove-orphans
$ ilo compose --runtime-cleanup-option=--remove-orphans --runtime-cleanup-option=--volumes

# do not specify custom option
$ ilo compose
```

By default, `--runtime-cleanup-option` is set to an empty array.
