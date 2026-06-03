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

## How a session is reused

`ilo compose` keeps your services running between runs. It brings the services up in the background (`up --detach`) and then enters the selected service with `exec`. When you exit, the services are **stopped but kept**, so the next run starts them again instead of recreating them — and anything written inside them persists.

When you want a clean slate, use [`--fresh`](#--fresh) to tear the services down (`down`) and recreate them on the next run.

## `--build`

The `--build` option can be used to build images before opening a new shell. Use this option in case you rely on local Dockerfiles.

```console
# build images before opening shell
$ ilo compose --build

# do not build images before opening shell
$ ilo compose --build=false

# do not build images before opening shell
$ ilo compose
```

By default, `--build` is not enabled.

## `--fresh`

The `--fresh` option discards the reused services and starts from a clean slate. `ilo` tears the services down (the equivalent of `compose down`) before bringing them back up, so the containers are recreated instead of restarted.

```console
# recreate the services from scratch
$ ilo compose --fresh

# reuse the running services (default)
$ ilo compose
```

By default, `--fresh` is not enabled.

## `--override-command`

So the attached service stays available for reuse across terminals, `ilo` runs it with a small keepalive (layered onto your compose file as an override, without editing it) and `exec`s into it. The services are then stopped only when the **last** attached session exits — so opening `ilo compose` from several terminals never interrupts the others, and there is no flag to manage. This needs a shell and `sleep` in the service's image, which practically every image has.

For a service that already runs a long-lived process of its own, use `--no-override-command` to leave its compose-defined command in place and rely on that process instead; with the override off the services are stopped when you exit.

```console
# run the service with a keepalive, last session out stops it (default)
$ ilo compose

# keep the service's own command from the compose file instead
$ ilo compose --no-override-command
```

By default, `--override-command` is enabled.

## `--shell`

The `--shell` option sets the shell that `ilo` runs when it attaches to the selected service without an explicit command. Because the service is entered with `exec`, `ilo` has to name a shell to start; pick one the service image actually ships.

```console
# attach with bash
$ ilo compose --shell /bin/bash

# attach with the default POSIX shell
$ ilo compose
```

By default, `--shell` is `/bin/sh`. When you pass a command to run instead (for example `ilo compose dev ls -la`), that command is used and `--shell` is ignored.

## `--debug`

The `--debug` option toggles whether `ilo` should print the runtime commands into your terminal before executing them. This can be useful in case you want to move away from `ilo` and just use your preferred runtime instead.

```console
# print runtime commands
$ ilo compose --debug

# do not print runtime commands
$ ilo compose --debug=false
$ ilo compose
```

By default, `--debug` is not enabled.

## `--file`

The `--file` option can be used to specify the file that contains your container definitions, e.g. a `docker-compose.yml` file.

```console
# use custom location
$ ilo compose --file /path/to/some/file.yaml

# use default location
$ ilo compose
```

By default, `--file` is set to `docker-compose.yml`.

## `--interactive`

The `--interactive` option can be used to control whether you want an interactive session (default) or just want to execute a single command (e.g. during CI builds).

```console
# run interactively
$ ilo compose --interactive

# run non-interactive
$ ilo compose --no-interactive
$ ilo compose --interactive=false

# run interactively
$ ilo compose
```

By default, `--interactive` is enabled.

## `--pull`

The `--pull` option can be used to pull images before opening a new shell. This is especially useful for teams using a `latest` tag for their image. The image will only pulled in case the registry contains a newer image than locally available.

```console
# pull image before opening shell
$ ilo compose --pull

# do not pull image before opening shell
$ ilo compose --pull=false

# do not pull image before opening shell
$ ilo compose
```

By default, `--pull` is not enabled.

## `--runtime`

The `--runtime` option can be used to force the usage of a specific runtime. See [runtimes](../runtimes) for details.

```console
# force to use docker-compose
$ ilo compose --runtime docker-compose

# force to use podman-compose
$ ilo compose --runtime podman-compose

# auto select
$ ilo compose
```

## `--runtime-option`

The `--runtime-option` option can be specified multiple times and contains options for the underlying runtime.

```console
# specify custom option(s)
$ ilo compose --runtime-option=--no-ansi
$ ilo compose --runtime-option=--no-ansi --runtime-option=--tls

# do not specify custom option
$ ilo compose
```

By default, `--runtime-option` is set to an empty array.

## `--runtime-pull-option`

The `--runtime-pull-option` can be specified multiple times and contains options for the runtimes' `pull` command.

```console
# specify custom option(s)
$ ilo compose --runtime-pull-option=--ignore-pull-failures
$ ilo compose --runtime-pull-option=--ignore-pull-failures --runtime-pull-option=--quiet

# do not specify custom option
$ ilo compose
```

By default, `--runtime-pull-option` is set to an empty array.

## `--runtime-build-option`

The `--runtime-build-option` can be specified multiple times and contains options for the runtimes' `build` command.

```console
# specify custom option(s)
$ ilo compose --runtime-build-option=--compress
$ ilo compose --runtime-build-option=--compress --runtime-build-option=--no-cache

# do not specify custom option
$ ilo compose
```

By default, `--runtime-build-option` is set to an empty array.

## `--runtime-run-option`

The `--runtime-run-option` can be specified multiple times and contains options for the runtimes' `run` command.

```console
# specify custom option(s)
$ ilo compose --runtime-run-option=--no-deps
$ ilo compose --runtime-run-option=--no-deps --runtime-run-option=--use-aliases

# do not specify custom option
$ ilo compose
```

By default, `--runtime-run-option` is set to an empty array.

## `--runtime-cleanup-option`

The `--runtime-cleanup-option` can be specified multiple times and contains options for the runtimes' cleanup command, e.g. `down` for `docker-compose`.

```console
# specify custom option(s)
$ ilo compose --runtime-cleanup-option=--remove-orphans
$ ilo compose --runtime-cleanup-option=--remove-orphans --runtime-cleanup-option=--volumes

# do not specify custom option
$ ilo compose
```

By default, `--runtime-cleanup-option` is set to an empty array.
